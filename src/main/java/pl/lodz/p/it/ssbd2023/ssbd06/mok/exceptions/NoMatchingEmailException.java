package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class NoMatchingEmailException extends ApplicationBaseException {
    public NoMatchingEmailException() {
        super(BAD_REQUEST, ERROR_EMAIL_DO_NOT_MATCH);
    }

}
