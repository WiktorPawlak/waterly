package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.StartingValue;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class CreateMainWaterMeterDto {

    @StartingValue
    private BigDecimal startingValue;
    @ExpiryDate
    private String expiryDate;
}
