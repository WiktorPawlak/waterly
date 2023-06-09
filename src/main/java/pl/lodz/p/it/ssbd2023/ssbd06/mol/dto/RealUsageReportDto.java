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
public class RealUsageReportDto {
    @NotNull
    private BigDecimal garbageCost;
    private BigDecimal garbageBalance;

    @NotNull
    private BigDecimal coldWaterCost;
    private BigDecimal coldWaterBalance;

    @NotNull
    private BigDecimal hotWaterCost;
    private BigDecimal hotWaterbalance;

    private BigDecimal unbilledWaterCost;
    @NotNull
    private BigDecimal coldWaterUsage;
    @NotNull
    private BigDecimal hotWaterUsage;
    private BigDecimal unbilledWaterAmount;

    public RealUsageReportDto(final Bill bill) {
        this.garbageCost = bill.getRealUsage().getGarbageCost();
        this.garbageBalance = bill.getAdvanceUsage().getGarbageCost().subtract(bill.getRealUsage().getGarbageCost());
        this.coldWaterCost = bill.getRealUsage().getColdWaterCost();
        this.coldWaterBalance = bill.getAdvanceUsage().getColdWaterCost().subtract(bill.getRealUsage().getColdWaterCost());
        this.hotWaterCost = bill.getRealUsage().getHotWaterCost();
        this.hotWaterbalance = bill.getAdvanceUsage().getHotWaterCost().subtract(bill.getRealUsage().getHotWaterCost());
        this.unbilledWaterCost = bill.getRealUsage().getUnbilledWaterCost();
        this.coldWaterUsage = bill.getRealUsage().getColdWaterUsage();
        this.hotWaterUsage = bill.getRealUsage().getHotWaterUsage();
        this.unbilledWaterAmount = bill.getRealUsage().getUnbilledWaterAmount();
    }
}
