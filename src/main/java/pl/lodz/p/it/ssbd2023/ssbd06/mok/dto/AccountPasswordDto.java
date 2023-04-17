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

    @NotNull(message = "oldPassword cannot be null")
    @Password
    private String oldPassword;

    @NotNull(message = "newPassword cannot be null")
    @Password
    private String newPassword;
}
