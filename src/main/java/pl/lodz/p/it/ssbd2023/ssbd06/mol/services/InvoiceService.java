package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService.ONE_MONTH;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ApartmentFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.InvoiceFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.TariffFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterCheckFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class InvoiceService {

    @Inject
    private InvoiceFacade invoiceFacade;

    @Inject
    private ApartmentFacade apartmentFacade;

    @Inject
    private WaterMeterCheckFacade waterMeterCheckFacade;

    @Inject
    private TariffFacade tariffFacade;

    @RolesAllowed({FACILITY_MANAGER})
    public void createInvoice(final CreateInvoiceDto invoice) {
        LocalDate invoiceDate = DateConverter.convertInvoiceDate(invoice.getDate());
        checkIfAllWaterMeterChecksArePresent(invoiceDate);
        checkIfTariffIsPresent(invoiceDate);
        invoiceFacade.create(new Invoice(invoice));
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateInvoice(final Invoice invoice, final InvoicesDto dto) {
        LocalDate invoiceDate = dto.getDate();
        checkIfTariffIsPresent(invoiceDate);
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setTotalCost(dto.getTotalCost());
        invoice.setWaterUsage(dto.getWaterUsage());
        Optional<Invoice> collidingInvoice = invoiceFacade.findInvoiceForYearMonth(dto.getDate());
        collidingInvoice.ifPresentOrElse(foundInvoice -> {
            if (foundInvoice.getId() != invoice.getId()) {
                throw ApplicationBaseException.invoicesCollidingException();
            }
        }, () -> invoice.setDate(dto.getDate()));
        invoiceFacade.update(invoice);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Invoice> getInvoices(final int page, final int pageSize, final String order, final String orderBy) {
        boolean ascOrder = "asc".equalsIgnoreCase(order);
        return invoiceFacade.findInvoices(page,
                pageSize,
                ascOrder,
                orderBy);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Long getInvoicesCount() {
        return invoiceFacade.count();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Invoice findInvoiceById(final long id) {
        return invoiceFacade.findById(id);
    }

    private void checkIfTariffIsPresent(final LocalDate invoiceDate) {
        tariffFacade.findTariffForYearMonth(invoiceDate).orElseThrow(ApplicationBaseException::tariffNotFoundForInvoice);
        tariffFacade.findTariffForYearMonth(invoiceDate.plusMonths(ONE_MONTH)).orElseThrow(ApplicationBaseException::tariffNotFoundForInvoice);
    }

    private void checkIfAllWaterMeterChecksArePresent(final LocalDate invoiceDate) {
        apartmentFacade.findAll().forEach(apartment -> {
            apartment.getWaterMeters().stream().filter(WaterMeter::isActive).forEach(waterMeter -> {
                waterMeterCheckFacade.findWaterMeterCheckByDateAndWaterMeterId(invoiceDate.minusMonths(ONE_MONTH),
                        waterMeter.getId()).orElseThrow(ApplicationBaseException::notAllWaterMeterChecksHaveBeenPerformed);
            });
        });
    }
}
