package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy;

import java.math.BigDecimal;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.AbstractBillService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.UsageReport;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;

@Named("firstGeneration")
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class FirstBillGenerationPolicy extends AbstractBillService implements BillGenerationPolicy {

    @Override
    public void performBillOperations(final Bill bill, final BigDecimal totalApartmentsArea, final BigDecimal unbilledWaterAmount, final Tariff tariffForBill) {
        List<WaterMeter> apartmentWaterMeters = bill.getApartment().getWaterMeters();
        UsageReport advanceUsage = createAndFillAdvanceUsage(tariffForBill, apartmentWaterMeters, bill.getDate());
        bill.setAdvanceUsage(advanceUsage);
        billFacade.create(bill);
    }

}
