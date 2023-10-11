package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.WaterMeterCheckService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@Named("newOwner")
@RequestScoped
@RolesAllowed({FACILITY_MANAGER, OWNER})
public class NewOwnerPolicy implements WaterUsageStatsPolicy {

    @Inject
    private TimeProvider timeProvider;
    @Inject
    private WaterMeterCheckService waterMeterCheckService;

    @Override
    public BigDecimal calculateExpectedMonthWaterMeterUsage(final WaterMeterCheck newWaterMeterCheck) {
        final WaterMeterCheck previousWaterMeterCheck = findPreviousWaterMeterCheck(newWaterMeterCheck);

        final BigDecimal realUsage = calculateRealUsage(newWaterMeterCheck, previousWaterMeterCheck);
        final BigDecimal expectedDailyUsage = calculateExpectedDailyUsage(newWaterMeterCheck);

        return calculateExpectedMonthWaterMeterUsage(newWaterMeterCheck, realUsage, expectedDailyUsage);
    }

    private WaterMeterCheck findPreviousWaterMeterCheck(final WaterMeterCheck newWaterMeterCheck) {
        final LocalDate previousCheckMonth = newWaterMeterCheck.getCheckDate().minusMonths(WATER_METER_CHECK_MONTH_INTERVAL);
        return waterMeterCheckService
                .findWaterMeterCheckForCheckDate(previousCheckMonth, newWaterMeterCheck.getWaterMeter()).orElseThrow();
    }

    private static BigDecimal calculateRealUsage(final WaterMeterCheck newWaterMeterCheck, final WaterMeterCheck previousWaterMeterCheck) {
        return newWaterMeterCheck.getMeterReading().subtract(previousWaterMeterCheck.getMeterReading());
    }

    private static BigDecimal calculateExpectedDailyUsage(final WaterMeterCheck newWaterMeterCheck) {
        return newWaterMeterCheck.getWaterMeter().getExpectedDailyUsage();
    }

    private BigDecimal calculateExpectedMonthWaterMeterUsage(final WaterMeterCheck newWaterMeterCheck,
                                                             final BigDecimal realUsage,
                                                             final BigDecimal expectedDailyUsage) {
        BigDecimal daysRemainingInMonth = BigDecimal.valueOf(timeProvider.getDaysRemainingInMonth(newWaterMeterCheck.getCheckDate()),
                DIGITS_AFTER_WATER_METER_DECIMAL_POINT);
        BigDecimal expectedUsageForRestOfTheMonth = daysRemainingInMonth.multiply(expectedDailyUsage);
        return realUsage.add(expectedUsageForRestOfTheMonth);
    }
}
