package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceCreatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckAddedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@RequestScoped
public class GenerateBillsService extends AbstractBillService {

    public static final int ONE_MONTH = 1;

    @RolesAllowed(FACILITY_MANAGER)
    public void generateBillsOnInvoiceCreation(final InvoiceCreatedEvent event) {
        List<Apartment> apartmentList = apartmentFacade.findAll();
        Invoice invoice = invoiceFacade.findInvoiceForYearMonth(event.getInvoiceDate()).orElseThrow();
        BigDecimal totalApartmentsArea = calculateTotalApartmentsArea(apartmentList);
        List<WaterUsageStats> apartmensLastMonthWaterUsageStats = findAllApartmentsLastMonthUsageStats(event.getInvoiceDate(), apartmentList);

        BigDecimal totalWaterUsageFromLastMonth = calculateLastMonthTotalWaterUsage(apartmensLastMonthWaterUsageStats);
        BigDecimal totalUnbilledWaterAmount = calculateTotalUnbilledWaterAmount(invoice, totalWaterUsageFromLastMonth);

        apartmentList.forEach(apartment -> selectAndPerformPolicyOperations(event.getInvoiceDate().minusMonths(ONE_MONTH),
                apartment,
                totalApartmentsArea,
                totalUnbilledWaterAmount));
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void generateBillOnWaterMeterCheckEvent(final WaterMeterCheckAddedEvent event) {
        List<Apartment> apartmentsForUpdatedWaterMeters = waterMeterFacade.findApartmentsByWaterMeterIds(convertDtoToWaterMeterIds(event));
        apartmentsForUpdatedWaterMeters.forEach(apartment -> selectAndPerformPolicyOperations(event.getCheckDate(),
                apartment,
                null,
                null));
    }

}
