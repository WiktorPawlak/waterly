package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedUsageReportDto {
    @NotNull
    private BigDecimal garbageCost;
    @NotNull
    private BigDecimal coldWaterCost;
    @NotNull
    private BigDecimal hotWaterCost;
    @NotNull
    private BigDecimal coldWaterUsage;
    @NotNull
    private BigDecimal hotWaterUsage;

    public AdvancedUsageReportDto(final Bill bill) {
        this.garbageCost = bill.getAdvanceUsage().getGarbageCost();
        this.coldWaterCost = bill.getAdvanceUsage().getColdWaterCost();
        this.hotWaterCost = bill.getAdvanceUsage().getHotWaterCost();
        this.coldWaterUsage = bill.getAdvanceUsage().getColdWaterUsage();
        this.hotWaterUsage = bill.getAdvanceUsage().getHotWaterUsage();
    }
}
