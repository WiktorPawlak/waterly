package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterChecksDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckAddedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckUpdatedEvent;
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
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingBean;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
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
    private AuthenticatedAccount callerContext;
    @Inject
    private MolAccountService molAccountService;
    @Inject
    private TimeProvider timeProvider;
    @Inject
    private WaterUsageStatsPolicyFactory waterUsageStatsPolicyFactory;
    @Inject
    private Event<WaterMeterCheckAddedEvent> waterMeterCheckAddedEventEvent;
    @Inject
    private Event<WaterMeterCheckUpdatedEvent> waterMeterCheckUpdatedEventEvent;

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void performWaterMeterChecks(final WaterMeterChecksDto dto) {
        boolean currentMonthChecksExists = currentChecksArePresent(dto);

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
        var usageStats = getWaterUsageStatsForNewChecks(check);

        upsertWaterUsageStats(expectedMonthHotWaterUsage, expectedMonthColdWaterUsage, check, usageStats);
        if (usageStats.isEmpty() || !currentMonthChecksExists) {
            waterMeterCheckAddedEventEvent.fire(new WaterMeterCheckAddedEvent(check.getCheckDate(), dto));
        }
    }

    private boolean currentChecksArePresent(final WaterMeterChecksDto dto) {
        List<WaterMeter> waterMeters = new ArrayList<>();
        dto.getWaterMeterChecks().forEach(dtoCheck -> {
            waterMeters.add(waterMeterService.findWaterMeterById(dtoCheck.getWaterMeterId()));
        });
        return waterMeters.stream()
                .allMatch(waterMeter -> waterMeterCheckService.findWaterMeterCheckForCheckDate(LocalDate.parse(dto.getCheckDate()), waterMeter).isPresent());
    }

    @SneakyThrows(ParseException.class)
    private List<WaterMeterCheck> prepareWaterMeterChecks(final WaterMeterChecksDto dto) {
        boolean managerAuthored = callerContext.isFacilityManager();
        final LocalDate checkDate = managerAuthored ? DateConverter.convertStringDateToLocalDate(dto.getCheckDate()) : timeProvider.currentLocalDate();

        return dto.getWaterMeterChecks().stream().map(checkDto -> prepareWaterMeterCheck(checkDto, checkDate, managerAuthored)).toList();
    }

    private WaterMeterCheck prepareWaterMeterCheck(final WaterMeterCheckDto dto, final LocalDate checkDate, final boolean managerAuthored) {
        var waterMeter = waterMeterService.findWaterMeterById(dto.getWaterMeterId());

        if (!managerAuthored) {
            checkWaterMeterBelongsToOwner(waterMeter);
            checkCheckDateIsCurrent(checkDate);
        }
        checkWaterMeterIsNotMain(waterMeter);

        return WaterMeterCheck.builder().meterReading(dto.getReading()).checkDate(checkDate).managerAuthored(managerAuthored).waterMeter(waterMeter).build();
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
