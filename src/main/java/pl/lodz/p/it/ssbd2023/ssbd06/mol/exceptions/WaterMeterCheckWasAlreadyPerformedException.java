package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class WaterMeterCheckWasAlreadyPerformedException extends ApplicationBaseException {

    public WaterMeterCheckWasAlreadyPerformedException() {
        super(BAD_REQUEST, ERROR_CHECK_WAS_ALREADY_PERFORMED);
    }
}
