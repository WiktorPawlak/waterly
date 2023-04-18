package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.response.ErrorResponse;

public class UnmatchedPasswordsException extends ApplicationBaseException {

    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";

    public UnmatchedPasswordsException() {
        super(Response.status(BAD_REQUEST).entity(new ErrorResponse(PASSWORDS_DO_NOT_MATCH)).build());
    }

}
