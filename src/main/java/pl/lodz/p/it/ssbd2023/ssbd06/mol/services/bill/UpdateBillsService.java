package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill;

import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService.ONE_MONTH;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateful
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UpdateBillsService extends AbstractBillService {

    @RolesAllowed(FACILITY_MANAGER)
    public void updateBillsOnInvoiceUpdate(final InvoiceUpdatedEvent event) {
        List<Apartment> apartmentList = apartmentFacade.findAll();
        List<Bill> billsToUpdate = billFacade.findBillsByDate(event.getInvoiceDate().minusMonths(ONE_MONTH));
        Invoice invoice = invoiceFacade.findInvoiceForYearMonth(event.getInvoiceDate()).orElseThrow();
        BigDecimal totalApartmentsArea = calculateTotalApartmentsArea(apartmentList);
        List<WaterUsageStats> apartmentsLastMonthWaterUsageStats = findAllApartmentsLastMonthUsageStats(event.getInvoiceDate(), apartmentList);

        BigDecimal totalWaterUsageFromLastMonth = calculateLastMonthTotalWaterUsage(apartmentsLastMonthWaterUsageStats);
        BigDecimal totalUnbilledWaterAmount = calculateTotalUnbilledWaterAmount(invoice, totalWaterUsageFromLastMonth);

        billsToUpdate.forEach(bill -> performBillUpdate(bill, totalApartmentsArea, totalUnbilledWaterAmount));
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void updateBillsOnTariffUpdate() {
        List<Bill> billsToUpdate = billFacade.findBillsWithNullRealUsage();
        billsToUpdate.forEach(this::performBillUpdate);
    }

}
