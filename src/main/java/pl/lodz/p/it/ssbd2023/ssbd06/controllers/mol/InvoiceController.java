package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.InvoiceEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadSigner;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.InvoicesOrderBy;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Order;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Page;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PageSize;

@Path("/invoices")
@RequestScoped
@Transactional
public class InvoiceController {

    @Inject
    private InvoiceEndpoint invoiceEndpoint;

    @Inject
    private PayloadSigner payloadSigner;

    @GET
    @RolesAllowed(FACILITY_MANAGER)
    public Response getInvoices(@Page @QueryParam("page") final Integer page,
                                @PageSize @QueryParam("pageSize") final Integer pageSize,
                                @Order @QueryParam("order") final String order,
                                @InvoicesOrderBy @QueryParam("orderBy") final String orderBy,
                                @QueryParam("pattern") final String pattern) {

        PaginatedList<InvoicesDto> invoices = invoiceEndpoint.getInvoicesList(pattern, page, pageSize, order, orderBy);
        return Response.ok().entity(invoices).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({FACILITY_MANAGER})
    public Response getInvoiceById(@PathParam("id") final long id) {
        InvoicesDto invoice = invoiceEndpoint.getInvoiceById(id);
        return Response.ok().entity(invoice).header("ETag", payloadSigner.sign(invoice)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({FACILITY_MANAGER})
    public Response updateInvoice(@PathParam("id") final long id, @NotNull @Valid final InvoicesDto dto) {
        invoiceEndpoint.updateInvoice(id, dto);
        return Response.ok().build();
    }

    @POST
    @RolesAllowed(FACILITY_MANAGER)
    public Response addInvoice(@NotNull @Valid final CreateInvoiceDto dto) {
        invoiceEndpoint.addInvoice(dto);
        return Response.status(CREATED).build();
    }
}
