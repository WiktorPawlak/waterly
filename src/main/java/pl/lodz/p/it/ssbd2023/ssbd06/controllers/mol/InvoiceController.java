package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateInvoiceDto;

@Path("/invoices")
@RequestScoped
public class InvoiceController extends RepeatableTransactionController {

    @GET
    public List<InvoicesDto> getInvoices() {
        throw new NotSupportedException();
    }

    @PUT
    @Path("/{id}")
    public void updateInvoice(@PathParam("id") final long id, @NotNull @Valid final UpdateInvoiceDto dto) {
        throw new NotSupportedException();
    }

    @POST
    public void addInvoice(@NotNull @Valid final CreateInvoiceDto dto) {
        throw new NotSupportedException();
    }
}
