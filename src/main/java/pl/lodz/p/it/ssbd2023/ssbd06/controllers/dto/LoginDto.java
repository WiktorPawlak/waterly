package pl.lodz.p.it.ssbd2023.ssbd06.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto {

    @NotBlank
    String login;
    @NotBlank
    String password;
}
