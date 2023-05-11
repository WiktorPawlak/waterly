package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

public class PersistenceConstraintException extends ApplicationBaseException {
    public PersistenceConstraintException(final String message) {
        super(CONFLICT, message);
    }
}
