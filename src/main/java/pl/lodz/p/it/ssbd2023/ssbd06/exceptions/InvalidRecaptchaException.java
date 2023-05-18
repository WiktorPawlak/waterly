package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidRecaptchaException extends ApplicationBaseException{
    public InvalidRecaptchaException() {
        super(BAD_REQUEST, ERROR_IF_RECAPTCHA_INVALID);
    }
}
