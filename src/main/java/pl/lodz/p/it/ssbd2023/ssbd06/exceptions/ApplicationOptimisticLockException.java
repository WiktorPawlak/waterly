package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

public class ApplicationOptimisticLockException extends ApplicationBaseException {
    public ApplicationOptimisticLockException() {
        super(CONFLICT, ERROR_OPTIMISTIC_LOCK);
    }
}
