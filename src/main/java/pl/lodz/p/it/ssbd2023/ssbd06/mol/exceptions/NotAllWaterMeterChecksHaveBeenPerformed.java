package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class NotAllWaterMeterChecksHaveBeenPerformed extends ApplicationBaseException {
    public NotAllWaterMeterChecksHaveBeenPerformed() {
        super(CONFLICT, ERROR_NOT_ALL_WATER_METER_CHECKS_PERFORMED);
    }
}
