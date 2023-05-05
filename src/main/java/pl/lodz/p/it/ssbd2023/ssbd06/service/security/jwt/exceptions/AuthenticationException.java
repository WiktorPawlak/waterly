package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class AuthenticationException extends ApplicationBaseException {
    public AuthenticationException() {
        super(UNAUTHORIZED, ERROR_AUTHENTICATION);
    }
}
