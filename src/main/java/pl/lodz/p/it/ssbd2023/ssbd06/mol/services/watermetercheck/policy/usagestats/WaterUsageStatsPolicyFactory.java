package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;

public interface WaterUsageStatsPolicyFactory {

    WaterUsageStatsPolicy createPolicy(WaterMeterCheck newWaterMeterCheck);
}
