package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.FirstName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.LastName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Login;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PhoneNumber;

@ToString
@Getter
@Setter
public class AccountDto {

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


}
