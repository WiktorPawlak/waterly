package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.check;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;

public class ManagerAuthoredCheckPolicy implements WaterMeterCheckPolicy {

    @Override
    public void apply(final WaterMeterCheck newCheck, final WaterMeterCheck existingCheck) {
        existingCheck.setMeterReading(newCheck.getMeterReading());
        existingCheck.setCheckDate(newCheck.getCheckDate());
        existingCheck.setManagerAuthored(true);
    }
}
