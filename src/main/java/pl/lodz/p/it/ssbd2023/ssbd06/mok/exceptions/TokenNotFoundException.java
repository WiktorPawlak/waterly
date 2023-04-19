package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.response.ErrorResponse;

public class TokenNotFoundException extends ApplicationBaseException {

    public static final String TOKEN_NOT_FOUND = "Token not found";

    public TokenNotFoundException() {
        super(Response.status(BAD_REQUEST).entity(new ErrorResponse(TOKEN_NOT_FOUND)).build());
    }

}
