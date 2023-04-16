package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

public class IdenticalPasswordsException extends Exception {

    public static final String PASSWORDS_CAN_NOT_BE_IDENTICAL = "Passwords can not be identical";

    private IdenticalPasswordsException(final String message) {
        super(message);
    }

    public static IdenticalPasswordsException identicalPasswordsException() {
        return new IdenticalPasswordsException(PASSWORDS_CAN_NOT_BE_IDENTICAL);
    }

}
