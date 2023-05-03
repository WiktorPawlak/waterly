package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

public class ResourceNotFoundException extends ApplicationBaseException {
    public ResourceNotFoundException() {
        super(NOT_FOUND, ERROR_RESOURCE_NOT_FOUND);
    }
}
