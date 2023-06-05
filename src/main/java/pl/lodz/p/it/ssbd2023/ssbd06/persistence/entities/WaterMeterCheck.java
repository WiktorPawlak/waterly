package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.CascadeType.REFRESH;
import static jakarta.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(
        name = "water_meter_check",
        indexes = {
                @Index(name = "water_meter_check_water_meter_idx", columnList = "water_meter_id")
        })
@NoArgsConstructor
@Getter
@EntityListeners({MolAuditingEntityListener.class})
public class WaterMeterCheck extends AbstractEntity {
    @NotNull
    @Column(nullable = false, precision = 8, scale = 3)
    private BigDecimal meterReading;
    @NotNull
    @Column(nullable = false)
    private LocalDate checkDate;
    @NotNull
    @Column(nullable = false)
    private boolean managerAuthored;
    @ToString.Exclude
    @NotNull
    @ManyToOne(cascade = REFRESH, fetch = LAZY)
    @JoinColumn(name = "water_meter_id", nullable = false, updatable = false)
    private WaterMeter waterMeter;
}
