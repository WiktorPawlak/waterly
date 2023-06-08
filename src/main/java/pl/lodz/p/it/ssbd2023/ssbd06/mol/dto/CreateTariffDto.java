package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Money;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CreateTariffDto {

    @Money
    private BigDecimal coldWaterPrice;
    @Money
    private BigDecimal hotWaterPrice;
    @Money
    private BigDecimal trashPrice;
    @NotNull
    @ExpiryDate
    private String startDate;
    @NotNull
    @ExpiryDate
    private String endDate;
}
