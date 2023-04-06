package pl.lodz.p.it.ssbd2023.ssbd06.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
@Getter
@NoArgsConstructor
public class Invoice extends AbstractEntity {
    @NotNull
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;
    @NotNull
    @Column(name = "water_usage", nullable = false)
    private BigDecimal waterUsage;
    @NotNull
    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;
    @NotNull
    @Column(nullable = false)
    private LocalDate date;
}
