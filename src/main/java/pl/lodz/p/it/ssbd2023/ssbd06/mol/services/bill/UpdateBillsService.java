package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill;

import static jakarta.enterprise.event.TransactionPhase.AFTER_COMPLETION;
import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService.ONE_MONTH;

import java.math.BigDecimal;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Observes;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.TariffUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class UpdateBillsService extends AbstractBillService {

    public void updateBillsOnInvoiceUpdate(@Observes(during = AFTER_COMPLETION) final InvoiceUpdatedEvent event) {
        List<Apartment> apartmentList = apartmentFacade.findAll();
        List<Bill> billsToUpdate = billFacade.findBillsByDate(event.getInvoiceDate().minusMonths(ONE_MONTH));
        Invoice invoice = invoiceFacade.findInvoiceForYearMonth(event.getInvoiceDate()).orElseThrow();
        BigDecimal totalApartmentsArea = calculateTotalApartmentsArea(apartmentList);
        List<WaterUsageStats> apartmensLastMonthWaterUsageStats = findAllApartmentsLastMonthUsageStats(event.getInvoiceDate(), apartmentList);

        BigDecimal totalWaterUsageFromLastMonth = calculateLastMonthTotalWaterUsage(apartmensLastMonthWaterUsageStats);
        BigDecimal totalUnbilledWaterAmount = calculateTotalUnbilledWaterAmount(invoice, totalWaterUsageFromLastMonth);

        billsToUpdate.forEach(bill -> performBillUpdate(bill, totalApartmentsArea, totalUnbilledWaterAmount));
    }

    public void updateBillsOnWaterMeterCheckUpdate(@Observes(during = AFTER_COMPLETION) final WaterMeterCheckUpdatedEvent event) {
        List<Apartment> apartmentsForUpdatedWaterMeters = waterMeterFacade.findApartmentsByWaterMeterIds(convertDtoToWaterMeterIds(event));
        apartmentsForUpdatedWaterMeters.forEach(apartment -> {
            Bill bill = billFacade.findByDateAndApartmentId(event.getCheckDate(), apartment.getId()).orElseThrow();
            performBillUpdate(bill);
        });

    }

    public void updateBillsOnTariffUpdate(@Observes(during = AFTER_COMPLETION) final TariffUpdatedEvent event) {
        List<Bill> billsToUpdate = billFacade.findBillsWithNullRealUsage();
        billsToUpdate.forEach(this::performBillUpdate);
    }

}
