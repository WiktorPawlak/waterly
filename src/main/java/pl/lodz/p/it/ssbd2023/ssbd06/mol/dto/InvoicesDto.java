package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.annotation.Nonnegative;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.Signable;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Money;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsage;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoicesDto implements Signable {

    @Nonnegative
    private long id;
    @NotNull
    private String invoiceNumber;
    @WaterUsage
    private BigDecimal waterUsage;
    @Money
    private BigDecimal totalCost;
    @NotNull
    private LocalDate date;
    @Nonnegative
    private long version;

    public InvoicesDto(final Invoice invoice) {
        this.id = invoice.getId();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.waterUsage = invoice.getWaterUsage();
        this.totalCost = invoice.getTotalCost();
        this.date = invoice.getDate();
        this.version = invoice.getVersion();
    }

    @Override
    public String createPayload() {
        return id + version + Invoice.class.getSimpleName();
    }
}
