package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class NoSuchBillException extends ApplicationBaseException {
    public NoSuchBillException() {
        super(NOT_FOUND, ERROR_NO_SUCH_BILL);
    }
}
