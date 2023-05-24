package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class AccountLockedException extends ApplicationBaseException {
    public AccountLockedException() {
        super(Response.Status.CONFLICT, ERROR_ACCOUNT_LOCKED);
    }
}
