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
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.check.ManagerAuthoredCheckPolicy;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.check.NonManagerAuthoredCheckPolicy;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.check.WaterMeterCheckPolicy;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(
        name = "water_meter_check",
        indexes = {
                @Index(name = "water_meter_check_water_meter_idx", columnList = "water_meter_id")
        })
@NamedQuery(
        name = "WaterMeterCheck.findCheckByDateAndWaterMeterType",
        query = "SELECT c FROM WaterMeterCheck c WHERE  YEAR(c.checkDate) = :year AND MONTH(c.checkDate) = :month AND c.waterMeter.type = :waterMeterType AND" +
                " c.waterMeter.active = true"
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EntityListeners({MolAuditingEntityListener.class})
public class WaterMeterCheck extends AbstractEntity {
    @Setter
    @NotNull
    @Column(name = "meter_reading", nullable = false, precision = 8, scale = 3)
    private BigDecimal meterReading;
    @Setter
    @NotNull
    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;
    @Setter
    @NotNull
    @Column(name = "manager_authored", nullable = false)
    private boolean managerAuthored;
    @ToString.Exclude
    @NotNull
    @ManyToOne(cascade = REFRESH, fetch = LAZY)
    @JoinColumn(name = "water_meter_id", nullable = false, updatable = false)
    private WaterMeter waterMeter;

    public void applyCheckPolicy(final WaterMeterCheck newCheck) {
        WaterMeterCheckPolicy policy;

        if (newCheck.isManagerAuthored()) {
            policy = new ManagerAuthoredCheckPolicy();
        } else {
            policy = new NonManagerAuthoredCheckPolicy();
        }

        policy.apply(newCheck, this);
    }
}
