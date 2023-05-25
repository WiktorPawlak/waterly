package pl.lodz.p.it.ssbd2023.ssbd06.mok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditEmailDto implements Signable {

    private long id;

    @Email
    private String email;

    private long version;

    @Override
    public String createPayload() {
        return String.valueOf(id + version);
    }

}
