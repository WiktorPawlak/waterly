package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth.ErrorResponse;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.ApplicationBaseException;

public class UnauthorizedException extends ApplicationBaseException {

    public UnauthorizedException(final String message) {
        super(Response.status(FORBIDDEN).entity(new ErrorResponse(message)).build());
    }
}
