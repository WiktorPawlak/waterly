package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class AccountNotFoundException extends ApplicationBaseException {
    public AccountNotFoundException() {
        super(NOT_FOUND, ERROR_ACCOUNT_NOT_FOUND);
    }
}
