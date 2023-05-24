package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

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

    @RolesAllowed(FACILITY_MANAGER)
    public void addInvoice(final CreateInvoiceDto dto) {
        //dto -> Invoice
        invoiceFacade.create(new Invoice());
    }
}
