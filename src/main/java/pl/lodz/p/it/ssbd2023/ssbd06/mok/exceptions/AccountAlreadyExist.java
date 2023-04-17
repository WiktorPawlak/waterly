package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

public class AccountAlreadyExist extends RuntimeException {
    public AccountAlreadyExist(final String message) {
        super(message);
    }

}
