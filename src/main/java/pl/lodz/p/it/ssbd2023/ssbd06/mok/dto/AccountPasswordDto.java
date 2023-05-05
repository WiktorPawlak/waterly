package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class AccountPasswordDto {

    @Password
    private String oldPassword;

    @Password
    private String newPassword;
}
