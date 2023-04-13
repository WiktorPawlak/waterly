package pl.lodz.p.it.ssbd2023.ssbd06.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Getter
@Table(name = "usage_report")
public class UsageReport extends AbstractEntity {
    @NotNull
    @Column(name = "garbage_cost")
    private BigDecimal garbageCost;
    @NotNull
    @Column(name = "cold_water_cost")
    private BigDecimal coldWaterCost;
    @NotNull
    @Column(name = "cold_water__usage")
    private BigDecimal coldWaterUsage;
    @NotNull
    @Column(name = "hot_water_cost")
    private BigDecimal hotWaterCost;
    @NotNull
    @Column(name = "hot_water_usage")
    private BigDecimal hotWaterUsage;
    @Column(name = "unbilled_water_cost")
    private BigDecimal unbilledWaterCost;
    @Column(name = "unbilled_water_amount")
    private BigDecimal unbilledWaterAmount;
}
