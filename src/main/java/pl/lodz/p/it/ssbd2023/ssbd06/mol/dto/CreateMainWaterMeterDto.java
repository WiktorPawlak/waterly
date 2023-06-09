package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CreateMainWaterMeterDto {

    @ExpiryDate
    private String expiryDate;

}
