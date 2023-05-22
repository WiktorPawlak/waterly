package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentBillsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillsDto;

@Path("/bills")
@RequestScoped
public class BillController extends RepeatableTransactionController {

    @GET
    public List<BillsDto> getBillsByOwnerId(@QueryParam("ownerId") final long ownerId) {
        throw new NotSupportedException();
    }

    @GET
    public List<ApartmentBillsDto> getBillsByApartmentId(@QueryParam("apartmentId") final long apartmentId) {
        throw new NotSupportedException();
    }

    @GET
    @Path("/{id}")
    public BillDto getBillById(@PathParam("id") final long billId) {
        throw new NotSupportedException();
    }
}
