package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy;

import java.math.BigDecimal;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.AbstractBillService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.UsageReport;

@Named("updateForecastPolicy")
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UpdateForecastPolicy extends AbstractBillService implements BillGenerationPolicy {

    @Override
    public void performBillOperations(final Bill bill, final BigDecimal totalApartmentsArea, final BigDecimal unbilledWaterAmount,
                                      final Tariff tariffForBills) {
        UsageReport forecast = bill.getAdvanceUsage();
        updateAdvanceUsage(forecast, tariffForBills, bill.getApartment(), bill.getDate());
        billFacade.update(bill);
    }
}
