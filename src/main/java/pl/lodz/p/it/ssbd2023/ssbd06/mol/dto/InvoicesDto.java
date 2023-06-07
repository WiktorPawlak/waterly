package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoicesDto {

        private long id;

        @NotNull
        private String invoiceNumber;
        @NotNull
        private BigDecimal waterUsage;
        @NotNull
        private BigDecimal totalCost;
        @NotNull
        private LocalDate date;


    public InvoicesDto(final Invoice invoice) {
            this.id = invoice.getId();
            this.invoiceNumber = invoice.getInvoiceNumber();
            this.waterUsage = invoice.getWaterUsage();
            this.totalCost = invoice.getTotalCost();
            this.date = invoice.getDate();
        }
    }
