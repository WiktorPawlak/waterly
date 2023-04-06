package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

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
    @OneToMany(mappedBy = "waterMeter", fetch = LAZY)
    private List<WaterMeterCheck> waterMeterChecks = new ArrayList<>();
    @NotNull
    @Column
    @Enumerated(EnumType.STRING)
    private WaterMeterType type;

    @ManyToOne
    @JoinColumn(name = "apartment_id", foreignKey = @ForeignKey(name = "water_meter_apartment_fk"))
    private Apartment apartment;
}
