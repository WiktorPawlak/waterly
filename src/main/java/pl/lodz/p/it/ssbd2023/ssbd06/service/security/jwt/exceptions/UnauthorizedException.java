package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class UnauthorizedException extends ApplicationBaseException {

    public UnauthorizedException(final String message) {
        super(FORBIDDEN, message);
    }
}
