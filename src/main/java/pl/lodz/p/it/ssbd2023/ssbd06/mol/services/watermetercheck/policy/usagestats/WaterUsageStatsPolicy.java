package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats;

import java.math.BigDecimal;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;

public interface WaterUsageStatsPolicy {
    int DIGITS_AFTER_WATER_METER_DECIMAL_POINT = 3;
    int WATER_METER_CHECK_MONTH_INTERVAL = 1;

    BigDecimal calculateExpectedMonthWaterMeterUsage(WaterMeterCheck newWaterMeterCheck);
}
