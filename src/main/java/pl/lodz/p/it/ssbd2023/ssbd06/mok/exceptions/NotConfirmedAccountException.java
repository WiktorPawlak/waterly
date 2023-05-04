package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class NotConfirmedAccountException extends ApplicationBaseException {
    public NotConfirmedAccountException() {
        super(CONFLICT, ERROR_ACCOUNT_NOT_CONFIRMED);
    }
}
