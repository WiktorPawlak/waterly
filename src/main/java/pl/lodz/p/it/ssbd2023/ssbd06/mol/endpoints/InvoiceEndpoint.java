package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.io.Serializable;
import java.util.List;

import io.vavr.Tuple2;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.config.PaginationConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceCreatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.InvoiceService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.UpdateBillsService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@RequestScoped
public class InvoiceEndpoint implements Serializable {

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private GenerateBillsService generateBillsService;

    @Inject
    private UpdateBillsService updateBillsService;

    @Inject
    private PaginationConfig paginationConfig;

    @SneakyThrows
    @RolesAllowed({FACILITY_MANAGER})
    public void addInvoice(final CreateInvoiceDto dto) {
        invoiceService.createInvoice(dto);
        InvoiceCreatedEvent event = new InvoiceCreatedEvent(DateConverter.convertInvoiceDate(dto.getDate()));
        generateBillsService.generateBillsOnInvoiceCreation(event);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateInvoice(final long id, final InvoicesDto dto) {
        Invoice invoice = invoiceService.findInvoiceById(id);
        if (dto.getVersion() != invoice.getVersion()) {
            throw ApplicationBaseException.optimisticLockException();
        }
        invoiceService.updateInvoice(invoice, dto);

        InvoiceUpdatedEvent event = new InvoiceUpdatedEvent(dto.getDate());
        updateBillsService.updateBillsOnInvoiceUpdate(event);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public PaginatedList<InvoicesDto> getInvoicesList(final String pattern,
                                                      final Integer page,
                                                      final Integer pageSize,
                                                      final String order,
                                                      final String orderBy) {

        int preparedPage = paginationConfig.preparePage(page);
        int preparedPageSize = paginationConfig.preparePageSize(pageSize);
        String preparedOrderBy = orderBy != null ? orderBy : "invoiceNumber";
        String preparedPattern = paginationConfig.preparePattern(pattern);
        boolean ascOrder = paginationConfig.prepareAscOrder(order);

        Tuple2<List<Invoice>, Long> paginatedInvoices =
                invoiceService.getInvoices(preparedPattern, preparedPage, preparedPageSize, ascOrder, preparedOrderBy);

        List<InvoicesDto> invoicesDtos = paginatedInvoices._1
                .stream().map(InvoicesDto::new)
                .toList();
        return new PaginatedList<>(
                invoicesDtos,
                preparedPage,
                invoicesDtos.size(),
                (long) Math.ceil(paginatedInvoices._2.doubleValue() / preparedPageSize)
        );
    }

    @RolesAllowed({FACILITY_MANAGER})
    public InvoicesDto getInvoiceById(final long id) {
        return new InvoicesDto(invoiceService.findInvoiceById(id));
    }
}
