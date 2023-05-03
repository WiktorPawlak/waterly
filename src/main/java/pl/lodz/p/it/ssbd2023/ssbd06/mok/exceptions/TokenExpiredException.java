package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

public class TokenExpiredException extends Exception {

    protected static final String ERROR_TOKEN_EXPIRED = "ERROR.TOKEN_EXPIRED";

    public TokenExpiredException() {
        super(ERROR_TOKEN_EXPIRED);
    }
}
