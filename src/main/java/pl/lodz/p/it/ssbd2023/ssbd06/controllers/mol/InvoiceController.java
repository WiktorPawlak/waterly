package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;


import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.GetPagedInvoicesListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.InvoiceEndpoint;

@Path("/invoices")
@RequestScoped
public class InvoiceController extends RepeatableTransactionController {

    @Inject
    private InvoiceEndpoint invoiceEndpoint;

    @POST
    @Path("/list")
    @RolesAllowed({FACILITY_MANAGER})
        public Response getInvoices(@NotNull @Valid final GetPagedInvoicesListDto dto) {
            PaginatedList<InvoicesDto> invoices = retry(() -> invoiceEndpoint.getInvoicesList(dto), invoiceEndpoint);
            return Response.ok().entity(invoices).build();
        }

    @PUT
    @Path("/{id}")
    @RolesAllowed({FACILITY_MANAGER})
    public void updateInvoice(@PathParam("id") final long id, @NotNull @Valid final UpdateInvoiceDto dto) {
        invoiceEndpoint.updateInvoice(id, dto);
    }

    @POST
    @RolesAllowed(FACILITY_MANAGER)
    public void addInvoice(@NotNull @Valid final CreateInvoiceDto dto) {
        invoiceEndpoint.addInvoice(dto);
    }
}
