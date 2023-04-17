package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth.ErrorResponse;

public class IdenticalPasswordsException extends ApplicationBaseException {

    public static final String PASSWORDS_CAN_NOT_BE_IDENTICAL = "Passwords can not be identical";

    public IdenticalPasswordsException() {
        super(Response.status(CONFLICT).entity(new ErrorResponse(PASSWORDS_CAN_NOT_BE_IDENTICAL)).build());
    }

}
