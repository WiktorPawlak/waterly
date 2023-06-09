package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;


public class InvalidDateException extends ApplicationBaseException {
    public InvalidDateException() {
        super(BAD_REQUEST, ERROR_INVALID_DATE);
    }
}
