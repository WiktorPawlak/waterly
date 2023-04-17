package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth.ErrorResponse;

public class CannotModifyPermissionsException extends ApplicationBaseException {

    public CannotModifyPermissionsException(final String message) {
        super(Response.status(CONFLICT).entity(new ErrorResponse(message)).build());
    }

}
