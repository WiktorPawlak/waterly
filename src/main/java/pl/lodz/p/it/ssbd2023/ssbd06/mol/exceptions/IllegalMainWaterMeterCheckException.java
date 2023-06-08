package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class IllegalMainWaterMeterCheckException extends ApplicationBaseException {

    public IllegalMainWaterMeterCheckException() {
        super(BAD_REQUEST, ERROR_ILLEGAL_MAIN_WATER_METER_CHECK);
    }
}
