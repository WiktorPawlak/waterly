package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.math.BigDecimal;
import java.time.YearMonth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Entity
@Table(
        name = "water_usage_stats",
        indexes = {
                @Index(name = "water_usage_stats_apartment_idx", columnList = "apartment_id")
        })
@NoArgsConstructor
@Getter
public class WaterUsageStats extends AbstractEntity {
    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false, foreignKey = @ForeignKey(name = "water_usage_stats_apartment_fk"))
    private Apartment apartment;
    @NotNull
    @Column(name = "year_month")
    private YearMonth yearMonth;
    @NotNull
    @Column(name = "hot_water_usage")
    private BigDecimal hotWaterUsage;
    @NotNull
    @Column(name = "cold_water_usage")
    private BigDecimal coldWaterUsage;
}
