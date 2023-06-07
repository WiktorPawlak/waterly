package pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class TariffsColidingException extends ApplicationBaseException {
    public TariffsColidingException() {
        super(CONFLICT, ERROR_TARIFFS_COLIDING);
    }
}
