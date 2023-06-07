package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class OwnerAccountDoesNotExistException extends ApplicationBaseException {

    public OwnerAccountDoesNotExistException() {
        super(NOT_FOUND, ERROR_OWNER_ACCOUNT_NOT_FOUND);
    }

}
