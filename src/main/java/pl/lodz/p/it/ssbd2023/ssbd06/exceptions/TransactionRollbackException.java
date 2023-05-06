package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

public class TransactionRollbackException extends ApplicationBaseException {
    public TransactionRollbackException() {
        super(INTERNAL_SERVER_ERROR, ERROR_TRANSACTION_ROLLBACK);
    }
}
