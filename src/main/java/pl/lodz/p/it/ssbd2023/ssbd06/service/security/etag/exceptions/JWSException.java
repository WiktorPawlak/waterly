package pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class JWSException extends ApplicationBaseException {
    public JWSException() {
        super(INTERNAL_SERVER_ERROR, ERROR_JWS_PROCESSING);
    }
}
