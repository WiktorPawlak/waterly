package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsage;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class WaterMeterExpectedUsagesDto {

    @Nonnegative
    private long waterMeterId;

    @WaterUsage
    private BigDecimal expectedMonthlyUsage;

}
