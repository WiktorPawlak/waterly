package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Credentials {

    @Login
    String login;

    @Password
    String password;
}
