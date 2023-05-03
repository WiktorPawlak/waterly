package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class AccountAlreadyExistException extends ApplicationBaseException {

    public AccountAlreadyExistException() {
        super(CONFLICT, ERROR_ACCOUNT_ALREADY_EXIST);
    }

}
