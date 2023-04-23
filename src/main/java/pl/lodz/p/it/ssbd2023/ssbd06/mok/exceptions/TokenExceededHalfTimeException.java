package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.response.ErrorResponse;

public class TokenExceededHalfTimeException extends ApplicationBaseException {

    public static final String TOKEN_EXCEEDED_HALF_TIME = "Token exceeded half time";

    public TokenExceededHalfTimeException() {
        super(Response.status(BAD_REQUEST).entity(new ErrorResponse(TOKEN_EXCEEDED_HALF_TIME)).build());
    }

}
