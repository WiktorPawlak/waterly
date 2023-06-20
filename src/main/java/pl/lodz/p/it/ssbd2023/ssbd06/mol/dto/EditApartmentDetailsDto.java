package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ApartmentArea;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ApartmentNumber;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditApartmentDetailsDto implements Signable {

    @Nonnegative
    long id;
    @ApartmentNumber
    String number;
    @ApartmentArea
    BigDecimal area;
    @Nonnegative
    long version;

    @Override
    public String createPayload() {
        return id + version + Apartment.class.getSimpleName();
    }
}
