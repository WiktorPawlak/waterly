package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.TwoFACode;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Credentials {

    @Login
    String login;

    @Password
    String password;

    @TwoFACode
    private String twoFACode;

    public Credentials(final String login, final String password) {
        this.login = login;
        this.password = password;
    }
}
