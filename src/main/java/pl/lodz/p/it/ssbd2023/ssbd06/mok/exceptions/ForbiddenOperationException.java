package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth.ErrorResponse;

public class ForbiddenOperationException extends ApplicationBaseException {
    public ForbiddenOperationException(final String message) {
        super(Response.status(FORBIDDEN).entity(new ErrorResponse(message)).build());
    }
}
