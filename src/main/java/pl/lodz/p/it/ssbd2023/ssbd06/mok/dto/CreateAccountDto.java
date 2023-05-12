package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.FirstName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.LanguageTag;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.LastName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PhoneNumber;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDto {
    @Login
    private String login;

    @Password
    private String password;

    @Email
    private String email;

    @FirstName
    private String firstName;

    @LastName
    private String lastName;

    @PhoneNumber
    private String phoneNumber;

    @LanguageTag
    private String languageTag;

}
