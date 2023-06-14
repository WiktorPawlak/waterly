package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.InvoiceDate;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.InvoiceNumber;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Money;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterUsage;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInvoiceDto {

    @InvoiceNumber
    private String invoiceNumber;
    @WaterUsage
    private BigDecimal waterUsage;
    @Money
    private BigDecimal totalCost;
    @InvoiceDate
    private String date;
}
