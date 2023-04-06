package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(
        name = "water_usage_stats",
        indexes = {
                @Index(name = "water_usage_stats_apartment_idx", columnList = "apartment_id")
        })
@NoArgsConstructor
@Getter
public class WaterUsageStats extends AbstractEntity {
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
