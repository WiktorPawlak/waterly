package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import static jakarta.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(
        name = "tariff",
        indexes = {
                @Index(
                        name = "entity_consistence_assurance_idx",
                        columnList = "entity_consistence_assurance_id"
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({MolAuditingEntityListener.class})
public class Tariff extends AbstractEntity {
    @NotNull
    @Column(name = "cold_water_price")
    private BigDecimal coldWaterPrice;
    @NotNull
    @Column(name = "hot_water_price")
    private BigDecimal hotWaterPrice;
    @NotNull
    @Column(name = "trash_price")
    private BigDecimal trashPrice;
    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate;
    @NotNull
    @Column(name = "end_date")
    private LocalDate endDate;
    @ToString.Exclude
    @ManyToOne(fetch = LAZY)
    @JoinColumn(
            name = "entity_consistence_assurance_id",
            foreignKey = @ForeignKey(name = "entity_consistence_assurance_fk")
    )
    private EntityConsistenceAssurance entityConsistenceAssurance;
}
