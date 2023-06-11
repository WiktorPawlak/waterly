package pl.lodz.p.it.ssbd2023.ssbd06.mol.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.annotation.Nonnegative;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Money;

@Data
@NoArgsConstructor
public class ApartmentBillDto {

    @Nonnegative
    private long billId;
    @NotNull
    private LocalDate billDate;
    @Money
    private BigDecimal balance;

    private ApartmentBillDto(final Bill bill) {
        this.billId = bill.getId();
        this.billDate = bill.getDate();
        this.balance = bill.getBalance();
    }

    public static ApartmentBillDto of(final Bill bill) {
        return new ApartmentBillDto(bill);
    }
}
