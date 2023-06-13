package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class InactiveWaterMeterException extends ApplicationBaseException {

    public InactiveWaterMeterException() {
        super(CONFLICT, ERROR_INACTIVE_WATER_METER);
    }

}
