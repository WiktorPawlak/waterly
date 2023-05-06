package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class CannotModifyPermissionsException extends ApplicationBaseException {

    public CannotModifyPermissionsException() {
        super(CONFLICT, ERROR_CANNOT_MODIFY_PERMISSIONS);
    }

}
