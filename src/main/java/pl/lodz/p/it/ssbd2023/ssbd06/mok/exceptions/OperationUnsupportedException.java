package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth.ErrorResponse;

public class OperationUnsupportedException extends ApplicationBaseException {
    public OperationUnsupportedException(final String message) {
        super(Response.status(BAD_REQUEST).entity(new ErrorResponse(message)).build());
    }
}
