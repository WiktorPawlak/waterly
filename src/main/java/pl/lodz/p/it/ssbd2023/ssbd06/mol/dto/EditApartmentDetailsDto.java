package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ApartmentArea;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ApartmentNumber;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditApartmentDetailsDto {

    @ApartmentNumber
    String number;
    @ApartmentArea
    BigDecimal area;

}
