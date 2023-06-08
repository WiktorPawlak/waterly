package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Money;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsage;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedUsageReportDto {

    @Money
    private BigDecimal garbageCost;
    @Money
    private BigDecimal coldWaterCost;
    @Money
    private BigDecimal hotWaterCost;
    @WaterUsage
    private BigDecimal coldWaterUsage;
    @WaterUsage
    private BigDecimal hotWaterUsage;

    public AdvancedUsageReportDto(final Bill bill) {
        this.garbageCost = bill.getAdvanceUsage().getGarbageCost();
        this.coldWaterCost = bill.getAdvanceUsage().getColdWaterCost();
        this.hotWaterCost = bill.getAdvanceUsage().getHotWaterCost();
        this.coldWaterUsage = bill.getAdvanceUsage().getColdWaterUsage();
        this.hotWaterUsage = bill.getAdvanceUsage().getHotWaterUsage();
    }
}
