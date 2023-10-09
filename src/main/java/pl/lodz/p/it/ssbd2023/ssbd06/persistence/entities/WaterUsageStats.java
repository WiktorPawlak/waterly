package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.math.BigDecimal;
import java.time.YearMonth;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.config.YearMonthDateAttributeConverter;

@ToString(callSuper = true)
@Entity
@Table(
        name = "water_usage_stats",
        indexes = {
                @Index(name = "water_usage_stats_apartment_idx", columnList = "apartment_id")
        })
@NamedQuery(name = "WaterUsageStats.findByApartmentIdAndYearMonth",
        query = "select w from WaterUsageStats w where w.apartment.id = :apartmentId and w.yearMonth = :yearMonth")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners({MolAuditingEntityListener.class})
public class WaterUsageStats extends AbstractEntity {
    @ToString.Exclude
    @NotNull
    @ManyToOne
    @JoinColumn(name = "apartment_id", nullable = false, foreignKey = @ForeignKey(name = "water_usage_stats_apartment_fk"))
    private Apartment apartment;
    @NotNull
    @Column(name = "date", columnDefinition = "date")
    @Convert(converter = YearMonthDateAttributeConverter.class)
    private YearMonth yearMonth;
    @Setter
    @NotNull
    @Column(name = "hot_water_usage", precision = 8, scale = 3)
    private BigDecimal hotWaterUsage;
    @Setter
    @NotNull
    @Column(name = "cold_water_usage", precision = 8, scale = 3)
    private BigDecimal coldWaterUsage;
}
