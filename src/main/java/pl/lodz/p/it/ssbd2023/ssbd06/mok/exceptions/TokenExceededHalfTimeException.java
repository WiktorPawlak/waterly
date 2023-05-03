package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class TokenExceededHalfTimeException extends ApplicationBaseException {

    public TokenExceededHalfTimeException() {
        super(BAD_REQUEST, ERROR_TOKEN_EXCEEDED_HALF_TIME);
    }

}
