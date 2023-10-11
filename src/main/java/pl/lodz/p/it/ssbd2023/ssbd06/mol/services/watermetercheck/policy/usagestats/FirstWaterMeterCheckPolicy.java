package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@Named("firstWaterMeterCheck")
@RequestScoped
@RolesAllowed({FACILITY_MANAGER, OWNER})
public class FirstWaterMeterCheckPolicy implements WaterUsageStatsPolicy {

    @Inject
    private TimeProvider timeProvider;

    @Override
    public BigDecimal calculateExpectedMonthWaterMeterUsage(final WaterMeterCheck newWaterMeterCheck) {
        BigDecimal realUsage = calculateRealUsage(newWaterMeterCheck);
        BigDecimal expectedDailyUsage = calculateExpectedDailyUsage(newWaterMeterCheck);

        return calculateExpectedMonthWaterMeterUsage(newWaterMeterCheck, realUsage, expectedDailyUsage);
    }

    private static BigDecimal calculateRealUsage(final WaterMeterCheck newWaterMeterCheck) {
        return newWaterMeterCheck.getMeterReading().subtract(newWaterMeterCheck.getWaterMeter().getStartingValue());
    }

    private static BigDecimal calculateExpectedDailyUsage(final WaterMeterCheck newWaterMeterCheck) {
        return newWaterMeterCheck.getWaterMeter().getExpectedDailyUsage();
    }

    private BigDecimal calculateExpectedMonthWaterMeterUsage(final WaterMeterCheck newWaterMeterCheck,
                                                             final BigDecimal realUsage,
                                                             final BigDecimal expectedDailyUsage) {
        BigDecimal daysRemainingInMonth = BigDecimal.valueOf(timeProvider.getDaysRemainingInMonth(newWaterMeterCheck.getCheckDate()));
        BigDecimal expectedUsageForRestOfTheMonth = daysRemainingInMonth.multiply(expectedDailyUsage);
        return realUsage.add(expectedUsageForRestOfTheMonth);
    }

}
