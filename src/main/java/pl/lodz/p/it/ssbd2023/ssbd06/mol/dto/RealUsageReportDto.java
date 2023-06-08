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
public class RealUsageReportDto {

    @Money
    private BigDecimal garbageCost;
    private BigDecimal garbageBalance;

    @Money
    private BigDecimal coldWaterCost;
    private BigDecimal coldWaterBalance;

    @Money
    private BigDecimal hotWaterCost;
    private BigDecimal hotWaterbalance;

    @WaterUsage
    private BigDecimal coldWaterUsage;
    @WaterUsage
    private BigDecimal hotWaterUsage;

    private BigDecimal unbilledWaterCost;
    private BigDecimal unbilledWaterAmount;

    public RealUsageReportDto(final Bill bill) {
        this.garbageCost = bill.getRealUsage().getGarbageCost();
        this.garbageBalance = bill.getAdvanceUsage().getGarbageCost().subtract(bill.getRealUsage().getGarbageCost());
        this.coldWaterCost = bill.getRealUsage().getColdWaterCost();
        this.coldWaterBalance = bill.getAdvanceUsage().getColdWaterCost().subtract(bill.getRealUsage().getColdWaterCost());
        this.hotWaterCost = bill.getRealUsage().getHotWaterCost();
        this.hotWaterbalance = bill.getAdvanceUsage().getHotWaterCost().subtract(bill.getRealUsage().getHotWaterCost());
        this.coldWaterUsage = bill.getRealUsage().getColdWaterUsage();
        this.hotWaterUsage = bill.getRealUsage().getHotWaterUsage();
        this.unbilledWaterCost = bill.getRealUsage().getUnbilledWaterCost();
        this.unbilledWaterAmount = bill.getRealUsage().getUnbilledWaterAmount();
    }
}
