package pl.lodz.p.it.ssbd2023.ssbd06.controllers.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ErrorResponse {

    String message;

    public ErrorResponse(final String message) {
        this.message = message;
    }

}
