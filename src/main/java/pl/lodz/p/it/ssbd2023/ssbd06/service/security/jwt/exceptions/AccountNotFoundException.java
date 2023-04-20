package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.response.ErrorResponse;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.ApplicationBaseException;

public class AccountNotFoundException extends ApplicationBaseException {
    public AccountNotFoundException(final String message) {
        super(Response.status(NOT_FOUND).entity(new ErrorResponse(message)).build());
    }
}
