package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDto {
    private long billId;
    @NotNull
    private BigDecimal balance;
    @NotNull
    private long apartmentId;
    @NotNull
    private long accountId;
    @NotNull
    private AdvancedUsageReportDto forecast;
    @NotNull
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
