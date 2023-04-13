package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.validators.Email;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.validators.FirstName;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.validators.LastName;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.validators.PhoneNumber;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDetailsDto {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    @FirstName
    private String firstName;
    @NotBlank
    @LastName
    private String lastName;
    @NotBlank
    @PhoneNumber
    private String phoneNumber;

    public AccountDetails toDomain() {
        return AccountDetails.builder()
                .email(email.toLowerCase())
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .build();
    }

}
