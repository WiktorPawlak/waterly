package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class TokenNotFoundException extends ApplicationBaseException {

    public TokenNotFoundException() {
        super(NOT_FOUND, ERROR_TOKEN_NOT_FOUND);
    }

}
