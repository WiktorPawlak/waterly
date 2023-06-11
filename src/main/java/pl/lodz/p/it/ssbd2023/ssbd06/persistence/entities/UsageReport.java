package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Getter
@Setter
@Table(name = "usage_report")
@EntityListeners({MolAuditingEntityListener.class})
public class UsageReport extends AbstractEntity {
    @NotNull
    @Column(name = "garbage_cost")
    private BigDecimal garbageCost;
    @NotNull
    @Column(name = "cold_water_cost")
    private BigDecimal coldWaterCost;
    @NotNull
    @Column(name = "cold_water__usage", precision = 8, scale = 3)
    private BigDecimal coldWaterUsage;
    @NotNull
    @Column(name = "hot_water_cost")
    private BigDecimal hotWaterCost;
    @NotNull
    @Column(name = "hot_water_usage", precision = 8, scale = 3)
    private BigDecimal hotWaterUsage;
    @Column(name = "unbilled_water_cost")
    private BigDecimal unbilledWaterCost;
    @Column(name = "unbilled_water_amount", precision = 8, scale = 3)
    private BigDecimal unbilledWaterAmount;
}
