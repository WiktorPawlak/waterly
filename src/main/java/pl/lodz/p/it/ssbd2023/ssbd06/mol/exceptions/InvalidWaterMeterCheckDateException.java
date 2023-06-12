package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class InvalidWaterMeterCheckDateException extends ApplicationBaseException {

    public InvalidWaterMeterCheckDateException() {
        super(BAD_REQUEST, ERROR_INVALID_CHECK_DATE);
    }
}
