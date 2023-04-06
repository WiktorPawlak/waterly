package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import static jakarta.persistence.CascadeType.REFRESH;

@Entity
@Table(
        name = "water_meter_check",
        indexes = {
                @Index(name = "water_meter_check_water_meter_idx", columnList = "water_meter_id")
        })
@NoArgsConstructor
@Getter
public class WaterMeterCheck extends AbstractEntity {
    @NotNull
    @Column(nullable = false)
    private BigDecimal meterReading;
    @NotNull
    @Column(nullable = false)
    private LocalDate checkDate;
    @NotNull
    @Column(nullable = false)
    private boolean managerAuthored;
    @NotNull
    @ManyToOne(cascade = REFRESH)
    @JoinColumn(name ="water_meter_id", nullable = false)
    private WaterMeter waterMeter;
}
