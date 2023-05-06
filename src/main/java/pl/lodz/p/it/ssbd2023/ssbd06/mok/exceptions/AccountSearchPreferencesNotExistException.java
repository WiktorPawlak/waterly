package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class AccountSearchPreferencesNotExistException extends ApplicationBaseException {
    public AccountSearchPreferencesNotExistException() {
        super(NOT_FOUND, ERROR_ACCOUNT_SEARCH_PREFERENCES_NOT_FOUND);
    }
}
