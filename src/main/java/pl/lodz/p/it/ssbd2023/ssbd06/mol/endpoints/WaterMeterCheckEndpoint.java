package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckAddedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.MolAccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterMeterService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterUsageStatsService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.WaterMeterCheckService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats.WaterUsageStatsPolicyFactory;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingBean;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WaterMeterCheckEndpoint extends TransactionBoundariesTracingBean {

    @Inject
    private WaterMeterCheckService waterMeterCheckService;
    @Inject
    private WaterMeterService waterMeterService;
    @Inject
    private WaterUsageStatsService waterUsageStatsService;
    @Inject
    private MolAccountService molAccountService;
    @Inject
    private TimeProvider timeProvider;
    @Inject
    private WaterUsageStatsPolicyFactory waterUsageStatsPolicyFactory;
    @Inject
    private GenerateBillsService generateBillsService;

    @RolesAllowed(OWNER)
    public void initializePerformWaterMeterChecksByOwner(final WaterMeterChecksDto dto) {
        var newWaterMeterChecks = prepareUnauthorizedWaterMeterChecks(dto);
        performWaterMeterChecks(dto, newWaterMeterChecks);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void initializePerformWaterMeterChecksByFM(final WaterMeterChecksDto dto) {
        var newWaterMeterChecks = prepareAuthorizedWaterMeterChecks(dto);
        performWaterMeterChecks(dto, newWaterMeterChecks);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void performWaterMeterChecks(final WaterMeterChecksDto dto, final List<WaterMeterCheck> newWaterMeterChecks) {
        var expectedMonthHotWaterUsage = BigDecimal.ZERO;
        var expectedMonthColdWaterUsage = BigDecimal.ZERO;
        boolean currentMonthChecksExists = currentChecksArePresent(dto);

        for (var newCheck : newWaterMeterChecks) {
            BigDecimal expectedMonthWaterMeterUsage = performWaterMeterCheck(newCheck);
            if (newCheck.getWaterMeter().getType().equals(WaterMeterType.HOT_WATER)) {
                expectedMonthHotWaterUsage = expectedMonthHotWaterUsage.add(expectedMonthWaterMeterUsage);
            } else {
                expectedMonthColdWaterUsage = expectedMonthColdWaterUsage.add(expectedMonthWaterMeterUsage);
            }
        }

        final WaterMeterCheck check = newWaterMeterChecks.get(0);
        var usageStats = getWaterUsageStatsForNewChecks(check);

        upsertWaterUsageStats(expectedMonthHotWaterUsage, expectedMonthColdWaterUsage, check, usageStats);
        if (usageStats.isEmpty() || !currentMonthChecksExists) {
            //tu był kiedyś event
            generateBillsService.generateBillOnWaterMeterCheckEvent(new WaterMeterCheckAddedEvent(check.getCheckDate(), dto));
        }
    }

    private boolean currentChecksArePresent(final WaterMeterChecksDto dto) {
        return dto.getWaterMeterChecks().stream()
                .map(checkDto -> waterMeterService.findWaterMeterById(checkDto.getWaterMeterId()))
                .allMatch(waterMeter -> waterMeterCheckService.findWaterMeterCheckForCheckDate(LocalDate.parse(dto.getCheckDate()), waterMeter).isPresent());
    }

    private List<WaterMeterCheck> prepareAuthorizedWaterMeterChecks(final WaterMeterChecksDto dto) {
        final LocalDate checkDate = DateConverter.convertStringDateToLocalDate(dto.getCheckDate());

        return dto.getWaterMeterChecks().stream().map(checkDto -> prepareAuthorizedWaterMeterCheck(checkDto, checkDate)).toList();
    }

    private List<WaterMeterCheck> prepareUnauthorizedWaterMeterChecks(final WaterMeterChecksDto dto) {
        final LocalDate checkDate = timeProvider.currentLocalDate();

        return dto.getWaterMeterChecks().stream().map(checkDto -> prepareUnauthorizedWaterMeterCheck(checkDto, checkDate)).toList();
    }

    private WaterMeterCheck prepareAuthorizedWaterMeterCheck(final WaterMeterCheckDto dto, final LocalDate checkDate) {
        var waterMeter = waterMeterService.findWaterMeterById(dto.getWaterMeterId());

        checkWaterMeterIsNotMain(waterMeter);

        return WaterMeterCheck.builder().meterReading(dto.getReading()).checkDate(checkDate).managerAuthored(true).waterMeter(waterMeter).build();
    }

    private WaterMeterCheck prepareUnauthorizedWaterMeterCheck(final WaterMeterCheckDto dto, final LocalDate checkDate) {
        var waterMeter = waterMeterService.findWaterMeterById(dto.getWaterMeterId());

        checkWaterMeterIsNotMain(waterMeter);
        checkWaterMeterBelongsToOwner(waterMeter);
        checkCheckDateIsCurrent(checkDate);

        return WaterMeterCheck.builder().meterReading(dto.getReading()).checkDate(checkDate).managerAuthored(false).waterMeter(waterMeter).build();
    }

    private void checkWaterMeterBelongsToOwner(final WaterMeter waterMeter) {
        if (waterMeter.getApartmentOwnerId() != molAccountService.getPrincipalId()) {
            throw ApplicationBaseException.waterMeterDoesNotBelongToOwnerException();
        }
    }

    private void checkCheckDateIsCurrent(final LocalDate checkDate) {
        if (!timeProvider.currentLocalDate().isEqual(checkDate)) {
            throw ApplicationBaseException.invalidWaterMeterCheckDateException();
        }
    }

    private static void checkWaterMeterIsNotMain(final WaterMeter waterMeter) {
        if (waterMeter.getType().equals(WaterMeterType.MAIN)) {
            throw ApplicationBaseException.illegalMainWaterMeterCheckException();
        }
    }

    private BigDecimal performWaterMeterCheck(final WaterMeterCheck newCheck) {
        checkWaterMeterExpired(newCheck);
        checkWaterMeterInactive(newCheck);

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

    private void checkWaterMeterInactive(final WaterMeterCheck newCheck) {
        if (!newCheck.getWaterMeter().isActive()) {
            throw ApplicationBaseException.inactiveWaterMeterException();
        }
    }

    private void upsertWaterMeterCheck(final WaterMeterCheck newCheck, final Optional<WaterMeterCheck> thisMonthCheck) {
        if (thisMonthCheck.isPresent()) {
            var waterMeterCheck = thisMonthCheck.get();
            waterMeterCheck.applyCheckPolicy(newCheck);
            waterMeterCheckService.updateWaterMeterCheck(waterMeterCheck);
        } else {
            checkFirstCheckNotLesserThenStartingValue(newCheck);
            waterMeterCheckService.addWaterMeterCheck(newCheck);
        }
    }

    private void checkFirstCheckNotLesserThenStartingValue(final WaterMeterCheck newCheck) {
        if (newCheck.getMeterReading().compareTo(newCheck.getWaterMeter().getStartingValue()) < 0) {
            throw ApplicationBaseException.waterMeterCheckLesserThenStartingValueException();
        }
    }

    private Optional<WaterUsageStats> getWaterUsageStatsForNewChecks(final WaterMeterCheck check) {
        final YearMonth yearMonthOfWaterMeterCheck = YearMonth.of(check.getCheckDate().getYear(), check.getCheckDate().getMonth());
        final Apartment waterMeterApartment = check.getWaterMeter().getApartment();
        return waterUsageStatsService.findByApartmentAndYearMonth(waterMeterApartment, yearMonthOfWaterMeterCheck);
    }

    private void upsertWaterUsageStats(final BigDecimal expectedMonthHotWaterUsage, final BigDecimal expectedMonthColdWaterUsage, final WaterMeterCheck check,
                                       final Optional<WaterUsageStats> usageStats) {
        WaterUsageStats newUsageStats;
        if (usageStats.isEmpty()) {
            newUsageStats = WaterUsageStats.builder().yearMonth(DateConverter.convert(check.getCheckDate())).apartment(check.getWaterMeter().getApartment())
                    .coldWaterUsage(expectedMonthColdWaterUsage).hotWaterUsage(expectedMonthHotWaterUsage).build();

            waterUsageStatsService.createWaterUsageStats(newUsageStats);
        } else {
            usageStats.get().setColdWaterUsage(expectedMonthColdWaterUsage);
            usageStats.get().setHotWaterUsage(expectedMonthHotWaterUsage);

            waterUsageStatsService.updateWaterUsageStats(usageStats.get());
        }
    }
}
