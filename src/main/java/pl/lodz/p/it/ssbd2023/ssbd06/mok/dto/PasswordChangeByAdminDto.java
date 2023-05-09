package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeByAdminDto {
    @Password
    private String newPassword;

}
