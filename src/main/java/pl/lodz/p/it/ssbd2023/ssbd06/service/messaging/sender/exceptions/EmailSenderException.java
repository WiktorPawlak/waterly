package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender.exceptions;

public class EmailSenderException extends RuntimeException {
    public EmailSenderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
