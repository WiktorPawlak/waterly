package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;


public class NotAuthorizedApplicationException extends ApplicationBaseException {
    public NotAuthorizedApplicationException() {
        super(UNAUTHORIZED, ERROR_NOT_AUTHORIZED);
    }
}
