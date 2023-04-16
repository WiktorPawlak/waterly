package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;

@Getter
@Setter
@NoArgsConstructor
public class AccountPasswordDto {

    @NotNull
    @Password
    private String oldPassword;

    @NotNull
    @Password
    private String newPassword;
}
