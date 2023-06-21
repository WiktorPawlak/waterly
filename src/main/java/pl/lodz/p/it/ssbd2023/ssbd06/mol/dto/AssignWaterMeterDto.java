package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ExpiryDate;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.SerialNumber;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.StartingValue;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterMeterType;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsageNullable;

@Data
@Builder
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class AssignWaterMeterDto {

    @SerialNumber
    private String serialNumber;
    @StartingValue
    private BigDecimal startingValue;
    @ExpiryDate
    private String expiryDate;
    @WaterMeterType
    private String type;
    @WaterUsageNullable
    private String expectedMonthlyUsage;
}
