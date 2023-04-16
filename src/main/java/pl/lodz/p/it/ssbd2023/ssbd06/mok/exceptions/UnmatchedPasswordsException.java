package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

public class UnmatchedPasswordsException extends Exception {

    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match";

    private UnmatchedPasswordsException(final String message) {
        super(message);
    }

    public static UnmatchedPasswordsException unmatchedPasswordsException() {
        return new UnmatchedPasswordsException(PASSWORDS_DO_NOT_MATCH);
    }
}
