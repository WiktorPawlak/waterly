package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditEmailDto {

    @Email
    private String email;

}
