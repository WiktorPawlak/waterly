package pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class EntityIntegrityViolatedException extends ApplicationBaseException {
    public EntityIntegrityViolatedException() {
        super(CONFLICT, ERROR_ENTITY_INTEGRITY_VIOLATED);
    }
}
