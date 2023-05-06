package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class ErrorResponse {

    String message;

    public ErrorResponse(final String message) {
        this.message = message;
    }

}
