package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterChecksDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.MolAccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterMeterService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterUsageStatsService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.WaterMeterCheckService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats.WaterUsageStatsPolicyFactory;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WaterMeterCheckEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private WaterMeterCheckService waterMeterCheckService;
    @Inject
    private WaterMeterService waterMeterService;
    @Inject
    private WaterUsageStatsService waterUsageStatsService;
    @Inject
    private AuthenticatedAccount callerContext;
    @Inject
    private MolAccountService molAccountService;
    @Inject
    private TimeProvider timeProvider;
    @Inject
    private WaterUsageStatsPolicyFactory waterUsageStatsPolicyFactory;

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void performWaterMeterChecks(final WaterMeterChecksDto dto) {
        var newWaterMeterChecks = prepareWaterMeterChecks(dto);
        var expectedMonthHotWaterUsage = BigDecimal.ZERO;
        var expectedMonthColdWaterUsage = BigDecimal.ZERO;

        for (var newCheck : newWaterMeterChecks) {
            BigDecimal expectedMonthWaterMeterUsage = performWaterMeterCheck(newCheck);
            if (newCheck.getWaterMeter().getType().equals(WaterMeterType.HOT_WATER)) {
                expectedMonthHotWaterUsage = expectedMonthHotWaterUsage.add(expectedMonthWaterMeterUsage);
            } else {
                expectedMonthColdWaterUsage = expectedMonthColdWaterUsage.add(expectedMonthWaterMeterUsage);
            }
        }

        final WaterMeterCheck check = newWaterMeterChecks.get(0);
        var usageStats = getWaterUsageStatsForWaterMeterCheck(check);

        upsertWaterUsageStats(expectedMonthHotWaterUsage, expectedMonthColdWaterUsage, check, usageStats);
    }

    private List<WaterMeterCheck> prepareWaterMeterChecks(final WaterMeterChecksDto dto) {
        return dto.getWaterMeterChecks().stream()
                .map(this::prepareWaterMeterCheck)
                .toList();
    }

    private WaterMeterCheck prepareWaterMeterCheck(final WaterMeterCheckDto dto) {
        var waterMeter = waterMeterService.findWaterMeterById(dto.getWaterMeterId());
        if (!callerContext.isFacilityManager()) {
            checkWaterMeterBelongsToOwner(waterMeter);
        }
//        if (!callerContext.isFacilityManager() && !timeProvider.currentLocalDate().isEqual(data-przekazana-jako-parametr)) {
//            //todo zgłosić wyjątek - OWNER może tylko w obecnym dniu wprowadzić pomiar, facilityManager tylko w tym miesiącu
//        }
        checkWaterMeterIsNotMain(waterMeter);

        return WaterMeterCheck.builder()
                .meterReading(dto.getReading())
                .checkDate(timeProvider.currentLocalDate())
                .managerAuthored(callerContext.isFacilityManager())
                .waterMeter(waterMeter)
                .build();
    }

    private void checkWaterMeterBelongsToOwner(final WaterMeter waterMeter) {
        if (waterMeter.getApartmentOwnerId() != molAccountService.getPrincipalId()) {
            throw ApplicationBaseException.waterMeterDoesNotBelongToOwnerException();
        }
    }

    private static void checkWaterMeterIsNotMain(final WaterMeter waterMeter) {
        if (waterMeter.getType().equals(WaterMeterType.MAIN)) {
            throw ApplicationBaseException.illegalMainWaterMeterCheckException();
        }
    }

    private BigDecimal performWaterMeterCheck(final WaterMeterCheck newCheck) {
        checkWaterMeterExpired(newCheck);

        var thisMonthCheck = waterMeterCheckService.findWaterMeterCheckForCheckDate(newCheck.getCheckDate(), newCheck.getWaterMeter());
        upsertWaterMeterCheck(newCheck, thisMonthCheck);

        var usageStatsPolicy = waterUsageStatsPolicyFactory.createPolicy(newCheck);

        return usageStatsPolicy.calculateExpectedMonthWaterMeterUsage(newCheck);
    }

    private void checkWaterMeterExpired(final WaterMeterCheck newCheck) {
        var checkDate = DateConverter.convertLocalDateToDate(newCheck.getCheckDate());
        if (!timeProvider.checkDateIsBeforeOtherDate(checkDate.toInstant(), newCheck.getWaterMeter().getExpiryDate().toInstant())) {
            throw ApplicationBaseException.waterMeterExpiredException();
        }
    }

    private void upsertWaterMeterCheck(final WaterMeterCheck newCheck, final Optional<WaterMeterCheck> thisMonthCheck) {
        if (thisMonthCheck.isPresent()) {
            var waterMeterCheck = thisMonthCheck.get();
            waterMeterCheck.applyCheckPolicy(newCheck);
            waterMeterCheckService.updateWaterMeterCheck(waterMeterCheck);
        } else {
            waterMeterCheckService.addWaterMeterCheck(newCheck);
        }
    }

    private Optional<WaterUsageStats> getWaterUsageStatsForWaterMeterCheck(final WaterMeterCheck check) {
        final YearMonth yearMonthOfWaterMeterCheck = YearMonth.of(check.getCheckDate().getYear(), check.getCheckDate().getMonth());
        final Apartment waterMeterApartment = check.getWaterMeter().getApartment();
        return waterUsageStatsService.findByApartmentAndYearMonth(waterMeterApartment, yearMonthOfWaterMeterCheck);
    }

    private void upsertWaterUsageStats(final BigDecimal expectedMonthHotWaterUsage,
                                       final BigDecimal expectedMonthColdWaterUsage,
                                       final WaterMeterCheck check,
                                       final Optional<WaterUsageStats> usageStats) {
        WaterUsageStats newUsageStats;
        if (usageStats.isEmpty()) {
            newUsageStats = WaterUsageStats.builder()
                    .yearMonth(DateConverter.convert(check.getCheckDate()))
                    .apartment(check.getWaterMeter().getApartment())
                    .coldWaterUsage(expectedMonthColdWaterUsage)
                    .hotWaterUsage(expectedMonthHotWaterUsage)
                    .build();
            waterUsageStatsService.createWaterUsageStats(newUsageStats);
        } else {
            usageStats.get().setColdWaterUsage(expectedMonthColdWaterUsage);
            usageStats.get().setHotWaterUsage(expectedMonthHotWaterUsage);

            waterUsageStatsService.updateWaterUsageStats(usageStats.get());
        }
    }
}
