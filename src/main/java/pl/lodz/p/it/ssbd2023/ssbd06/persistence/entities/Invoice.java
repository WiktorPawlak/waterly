package pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit.MolAuditingEntityListener;

@ToString(callSuper = true)
@Entity
@Table(name = "invoice",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"invoice_number"}, name = "uk_invoice_number"),
        })
@Getter
@Setter
@NoArgsConstructor
@EntityListeners({MolAuditingEntityListener.class})
public class Invoice extends AbstractEntity {
    @NotNull
    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;
    @NotNull
    @Column(name = "water_usage", nullable = false, precision = 8, scale = 3)
    private BigDecimal waterUsage;
    @NotNull
    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;
    @NotNull
    @Column(nullable = false)
    private LocalDate date;
}
