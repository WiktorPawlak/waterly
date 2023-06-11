package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService.FIRST_PAGE;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.GetPagedInvoicesListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceCreatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.InvoiceService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InvoiceEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private InvoiceService invoiceService;

    @Inject
    @Property("default.list.page.size")
    private int defaultListPageSize;

    @Inject
    private Event<InvoiceCreatedEvent> invoiceCreatedEventEvent;

    @Inject
    private Event<InvoiceUpdatedEvent> invoiceUpdatedEvent;

    @SneakyThrows
    @RolesAllowed({FACILITY_MANAGER})
    public void addInvoice(final CreateInvoiceDto dto) {
        invoiceService.createInvoice(dto);
        InvoiceCreatedEvent event = new InvoiceCreatedEvent(DateConverter.convertInvoiceDate(dto.getDate()));
        invoiceCreatedEventEvent.fire(event);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateInvoice(final long id, final InvoicesDto dto) {
        Invoice invoice = invoiceService.findInvoiceById(id);
        if (dto.getVersion() != invoice.getVersion()) {
            throw ApplicationBaseException.optimisticLockException();
        }
        invoiceService.updateInvoice(invoice, dto);

        InvoiceUpdatedEvent event = new InvoiceUpdatedEvent(dto.getDate());
        invoiceUpdatedEvent.fire(event);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public PaginatedList<InvoicesDto> getInvoicesList(final @NotNull @Valid GetPagedInvoicesListDto dto) {
        int pageResolved = dto.getPage() != null ? dto.getPage() : FIRST_PAGE;
        int pageSizeResolved = dto.getPageSize() != null ? dto.getPageSize() : defaultListPageSize;
        String orderByResolved = dto.getOrderBy() != null ? dto.getOrderBy() : "date";
        List<InvoicesDto> invoices = invoiceService.getInvoices(pageResolved,
                        pageSizeResolved,
                        dto.getOrder(),
                        orderByResolved).stream()
                .map(InvoicesDto::new)
                .toList();

        return new PaginatedList<>(invoices,
                pageResolved,
                invoices.size(),
                (long) Math.ceil(invoiceService.getInvoicesCount().doubleValue() / pageSizeResolved));
    }

    @RolesAllowed({FACILITY_MANAGER})
    public InvoicesDto getInvoiceById(final long id) {
        return new InvoicesDto(invoiceService.findInvoiceById(id));
    }
}
