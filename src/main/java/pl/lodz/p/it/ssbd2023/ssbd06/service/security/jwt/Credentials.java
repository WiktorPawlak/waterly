package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Credentials {

    @NotBlank
    String login;
    @NotBlank
    String password;
}
