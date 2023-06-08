package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import javax.annotation.Nonnegative;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Money;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDto {

    @Nonnegative
    private long billId;
    @Money
    private BigDecimal balance;
    @Nonnegative
    private long apartmentId;
    @Nonnegative
    private long accountId;
    @NotNull
    @Valid
    private AdvancedUsageReportDto forecast;
    @NotNull
    @Valid
    private RealUsageReportDto realUsage;

    public BillDto(final Bill bill) {
        this.billId = bill.getId();
        this.balance = bill.getBalance();
        this.apartmentId = bill.getApartment().getId();
        this.accountId = bill.getAccount().getId();
        this.forecast = new AdvancedUsageReportDto(bill);
        this.realUsage = new RealUsageReportDto(bill);
    }

}
