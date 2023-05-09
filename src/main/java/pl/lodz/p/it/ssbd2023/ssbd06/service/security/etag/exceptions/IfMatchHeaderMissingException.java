package pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class IfMatchHeaderMissingException extends ApplicationBaseException {
    public IfMatchHeaderMissingException() {
        super(BAD_REQUEST, ERROR_IF_MATCH_HEADER_MISSING);
    }
}
