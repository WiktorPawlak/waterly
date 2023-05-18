package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import static jakarta.ws.rs.core.Response.Status.ACCEPTED;

import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

public class TwoFARequestedException extends ApplicationBaseException {

    public TwoFARequestedException() {
        super(ACCEPTED, INFO_TWO_FA_CODE_REQUESTED);
    }
}
