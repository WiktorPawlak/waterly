package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class InvoicesCollidingException extends ApplicationBaseException {
    public InvoicesCollidingException() {
        super(CONFLICT, ERROR_INVOICES_COLLIDING);
    }
}
