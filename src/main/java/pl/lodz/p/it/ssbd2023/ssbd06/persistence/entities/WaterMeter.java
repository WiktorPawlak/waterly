package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(
        name = "water_meter",
        indexes = {
                @Index(name = "water_meter_apartment_idx", columnList = "apartment_id")
        }
)
@NamedQuery(name = "WaterMeter.findAllByType", query = "select w from WaterMeter w where w.type = :type")
@Getter
@NoArgsConstructor
@EntityListeners({MolAuditingEntityListener.class})
public class WaterMeter extends AbstractEntity {
    @NotNull
    @Column(name = "starting_value", nullable = false, precision = 8, scale = 3)
    private BigDecimal startingValue;
    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;
    @NotNull
    @Column(name = "expected_usage", nullable = false, precision = 8, scale = 3)
    private BigDecimal expectedUsage;
    @Setter
    @Column(nullable = false)
    private boolean active;
    @ToString.Exclude
    @OneToMany(mappedBy = "waterMeter", fetch = LAZY)
    private List<WaterMeterCheck> waterMeterChecks = new ArrayList<>();
    @NotNull
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private WaterMeterType type;
    @ToString.Exclude
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "apartment_id", foreignKey = @ForeignKey(name = "water_meter_apartment_fk"))
    private Apartment apartment;
}
