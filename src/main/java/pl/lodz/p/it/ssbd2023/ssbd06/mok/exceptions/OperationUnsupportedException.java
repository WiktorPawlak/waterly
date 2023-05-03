package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class OperationUnsupportedException extends ApplicationBaseException {
    public OperationUnsupportedException() {
        super(BAD_REQUEST, ERROR_UNSUPPORTED_OPERATION);
    }
}
