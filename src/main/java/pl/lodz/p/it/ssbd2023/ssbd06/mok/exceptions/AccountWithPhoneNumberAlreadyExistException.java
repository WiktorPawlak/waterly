package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class AccountWithPhoneNumberAlreadyExistException extends ApplicationBaseException {

    public AccountWithPhoneNumberAlreadyExistException() {
        super(CONFLICT, ERROR_ACCOUNT_WITH_PHONE_NUMBER_ALREADY_EXIST);
    }

}
