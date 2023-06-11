package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy;

import java.math.BigDecimal;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;

public interface BillGenerationPolicy {

    void performBillOperations(Bill bill, BigDecimal totalApartmentsArea, BigDecimal unbilledWaterAmount, Tariff tariffForBills);
}
