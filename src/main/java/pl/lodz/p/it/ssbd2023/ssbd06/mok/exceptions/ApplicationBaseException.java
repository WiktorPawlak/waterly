package pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;

public class ApplicationBaseException extends Exception {

    @Setter
    @Getter
    Response response;

    public ApplicationBaseException(final String message) {
        super(message);
    }

    public ApplicationBaseException(final Response response) {
        this.response = response;
    }
}
