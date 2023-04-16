package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import jakarta.ws.rs.ForbiddenException;

public class OperationForbiddenException extends ForbiddenException {

    public OperationForbiddenException(final String message) {
        super(message);
    }

    public OperationForbiddenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
