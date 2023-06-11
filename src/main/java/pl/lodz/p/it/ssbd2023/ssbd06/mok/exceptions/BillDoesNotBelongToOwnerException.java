package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class BillDoesNotBelongToOwnerException extends ApplicationBaseException {
    public BillDoesNotBelongToOwnerException() {
        super(BAD_REQUEST, ERROR_BILL_DOES_NOT_BELONG_TO_OWNER);
    }
}
