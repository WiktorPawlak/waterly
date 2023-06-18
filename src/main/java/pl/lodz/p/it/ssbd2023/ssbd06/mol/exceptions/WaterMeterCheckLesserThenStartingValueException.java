package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class WaterMeterCheckLesserThenStartingValueException extends ApplicationBaseException {

    public WaterMeterCheckLesserThenStartingValueException() {
        super(BAD_REQUEST, ERROR_NEW_METER_READING_LESSER_THEN_STARTING_VALUE);
    }
}
