package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class UnmatchedPasswordsException extends ApplicationBaseException {

    public UnmatchedPasswordsException() {
        super(BAD_REQUEST, ERROR_PASSWORDS_DO_NOT_MATCH);
    }

}
