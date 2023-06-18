package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class PreviousMeterReadingGreaterThenNewOneException extends ApplicationBaseException {

    public PreviousMeterReadingGreaterThenNewOneException() {
        super(BAD_REQUEST, ERROR_PREVIOUS_METER_READING_GREATER_THEN_NEW_ONE);
    }
}
