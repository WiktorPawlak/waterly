package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.response.ErrorResponse;

public class TokenNotFoundException extends ApplicationBaseException {

    public static final String TOKEN_NOT_FOUND = "Token not found";

    public TokenNotFoundException() {
        super(Response.status(NOT_FOUND).entity(new ErrorResponse(TOKEN_NOT_FOUND)).build());
    }

    public TokenNotFoundException(final String message) {
        super(Response.status(NOT_FOUND).entity(new ErrorResponse(message)).build());
    }
}
