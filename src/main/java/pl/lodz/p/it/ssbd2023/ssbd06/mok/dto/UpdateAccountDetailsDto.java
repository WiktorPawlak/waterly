package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.FirstName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.LastName;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PhoneNumber;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDetailsDto implements Signable {

    long id;

    @Email
    private String email;

    @FirstName
    private String firstName;

    @LastName
    private String lastName;

    @PhoneNumber
    private String phoneNumber;

    private long version;

    public AccountDetails toDomain() {
        return AccountDetails.builder()
                .email(email.toLowerCase())
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .build();
    }

    @Override
    public String createPayload() {
        return String.valueOf(id + version);
    }

}
