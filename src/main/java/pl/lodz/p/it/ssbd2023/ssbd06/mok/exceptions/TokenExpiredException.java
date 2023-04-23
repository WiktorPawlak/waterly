package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

public class TokenExpiredException extends Exception {

    public static final String TOKEN_EXPIRED = "Token expired";

    public TokenExpiredException() {
        super(TOKEN_EXPIRED);
    }
}
