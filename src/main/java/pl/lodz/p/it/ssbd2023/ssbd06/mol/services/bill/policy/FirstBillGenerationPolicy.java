package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy;

import java.math.BigDecimal;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.AbstractBillService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.UsageReport;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;

@Named("firstGeneration")
@RequestScoped
public class FirstBillGenerationPolicy extends AbstractBillService implements BillGenerationPolicy {

    @Override
    public void performBillOperations(final Bill bill,
                                      final BigDecimal totalApartmentsArea,
                                      final BigDecimal unbilledWaterAmount,
                                      final Tariff tariffForBill) {
        List<WaterMeter> apartmentWaterMeters = bill.getApartment().getWaterMeters().stream().filter(WaterMeter::isActive).toList();
        UsageReport advanceUsage = createAndFillAdvanceUsage(tariffForBill, apartmentWaterMeters, bill.getDate());
        bill.setAdvanceUsage(advanceUsage);
        billFacade.create(bill);
    }

}
