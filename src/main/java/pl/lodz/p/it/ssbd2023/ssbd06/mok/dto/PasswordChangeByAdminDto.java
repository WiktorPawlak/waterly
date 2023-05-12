package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeByAdminDto {
    @Password
    private String newPassword;

}
