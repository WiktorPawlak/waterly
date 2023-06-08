package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.check;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;

public interface WaterMeterCheckPolicy {

    void apply(WaterMeterCheck newCheck, WaterMeterCheck existingCheck);
}
