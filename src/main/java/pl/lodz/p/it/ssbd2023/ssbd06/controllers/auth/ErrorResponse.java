package pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse {

    List<String> message;

    public ErrorResponse(final String message) {
        this.message = List.of(message);
    }

}
