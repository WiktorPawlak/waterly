package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Credentials {

    @NotBlank
    @Login
    String login;
    @NotBlank
    @Password
    String password;
}
