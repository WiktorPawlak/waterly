package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;

public class ForbiddenOperationException extends ApplicationBaseException {
    public ForbiddenOperationException() {
        super(FORBIDDEN, ERROR_FORBIDDEN_OPERATION);
    }
}
