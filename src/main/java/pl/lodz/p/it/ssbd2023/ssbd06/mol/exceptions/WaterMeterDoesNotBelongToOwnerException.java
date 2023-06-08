package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class WaterMeterDoesNotBelongToOwnerException extends ApplicationBaseException {

    public WaterMeterDoesNotBelongToOwnerException() {
        super(BAD_REQUEST, ERROR_WATER_METER_DOES_NOT_BELONG_TO_OWNER);
    }
}
