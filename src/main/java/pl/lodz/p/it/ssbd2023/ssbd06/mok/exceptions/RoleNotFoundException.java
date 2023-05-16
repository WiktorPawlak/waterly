package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class RoleNotFoundException extends ApplicationBaseException {
    public RoleNotFoundException() {
        super(NOT_FOUND, ERROR_ROLE_NOT_FOUND);
    }
}
