package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentBillsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.BillEndpoint;

@Path("/bills")
@RequestScoped
public class BillController extends RepeatableTransactionController {

    @Inject
    private BillEndpoint billEndpoint;

    @GET
    @Path("/owner")
    public List<BillsDto> getBillsByOwnerId(@QueryParam("ownerId") final long ownerId) {
        throw new NotSupportedException();
    }

    @GET
    @Path("/apartment")
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<ApartmentBillsDto> getBillsByApartmentId(@QueryParam("apartmentId") final long apartmentId) {
        return billEndpoint.getBillsByApartmentId(apartmentId);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public BillDto getBillById(@PathParam("id") final long billId) {
        return billEndpoint.getBillById(billId);
    }
}
