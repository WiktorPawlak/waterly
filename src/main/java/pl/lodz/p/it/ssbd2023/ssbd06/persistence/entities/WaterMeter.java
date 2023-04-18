package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString(callSuper = true)
@Entity
@Table(
        name = "water_meter",
        indexes = {
                @Index(name = "water_meter_apartment_idx", columnList = "apartment_id")
        }
)
@Getter
@NoArgsConstructor
public class WaterMeter extends AbstractEntity {
    @NotNull
    @Column(nullable = false)
    private BigDecimal startingValue;
    @NotNull
    @Column(nullable = false)
    private Date epxiryDate;
    @NotNull
    @Column(nullable = false)
    private BigDecimal expectedUsage;
    @Column(nullable = false)
    private boolean active;
    @ToString.Exclude
    @OneToMany(mappedBy = "waterMeter", fetch = LAZY)
    private List<WaterMeterCheck> waterMeterChecks = new ArrayList<>();
    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private WaterMeterType type;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "apartment_id", foreignKey = @ForeignKey(name = "water_meter_apartment_fk"))
    private Apartment apartment;
}
