package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.check;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;

public class NonManagerAuthoredCheckPolicy implements WaterMeterCheckPolicy {

    @Override
    public void apply(final WaterMeterCheck newCheck, final WaterMeterCheck existingCheck) {
        if (existingCheck.isManagerAuthored()) {
            throw ApplicationBaseException.waterMeterCheckWasAlreadyPerformedException();
        }
        existingCheck.setMeterReading(newCheck.getMeterReading());
        existingCheck.setCheckDate(newCheck.getCheckDate());
        existingCheck.setManagerAuthored(false);
    }
}