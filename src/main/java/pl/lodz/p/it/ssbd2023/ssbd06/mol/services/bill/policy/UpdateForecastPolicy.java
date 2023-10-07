package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy;

import java.math.BigDecimal;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.AbstractBillService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.UsageReport;

@Named("updateForecastPolicy")
@RequestScoped
public class UpdateForecastPolicy extends AbstractBillService implements BillGenerationPolicy {

    @Override
    public void performBillOperations(final Bill bill, final BigDecimal totalApartmentsArea, final BigDecimal unbilledWaterAmount,
                                      final Tariff tariffForBills) {
        UsageReport forecast = bill.getAdvanceUsage();
        updateAdvanceUsage(forecast, tariffForBills, bill.getApartment(), bill.getDate());
        billFacade.update(bill);
    }
}
