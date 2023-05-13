package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class AccountWithEmailAlreadyExistException extends ApplicationBaseException {

    public AccountWithEmailAlreadyExistException(final String error) {
        super(CONFLICT, error);
    }

}
