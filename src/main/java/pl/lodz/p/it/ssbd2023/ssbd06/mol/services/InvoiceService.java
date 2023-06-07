package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.InvoiceFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class InvoiceService {

    @Inject
    private InvoiceFacade invoiceFacade;

    @Inject
    private GenerateBillsService generateBillsService;

    @RolesAllowed({FACILITY_MANAGER})
    public void createInvoice(final CreateInvoiceDto invoice) {
        //dto -> Invoice
        createInvoice();
        generateBillsService.generateBills(new Invoice());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void createInvoice() {
        invoiceFacade.create(new Invoice());
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateInvoice(final Invoice invoice) {
        invoiceFacade.update(invoice);
        //recalculate bills
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

}
