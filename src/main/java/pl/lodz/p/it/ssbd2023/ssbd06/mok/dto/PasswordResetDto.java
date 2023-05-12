package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Password;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ValidUUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDto {
    @NotNull
    @ValidUUID
    private String token;
    @NotNull
    @Password
    private String newPassword;
    @NotNull
    private TokenType type;
}

