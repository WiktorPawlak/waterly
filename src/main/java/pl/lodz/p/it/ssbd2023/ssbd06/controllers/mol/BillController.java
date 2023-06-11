package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentBillsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.BillEndpoint;

@Path("/bills")
@RequestScoped
public class BillController extends RepeatableTransactionController {

    @Inject
    private BillEndpoint billEndpoint;

    @GET
    @Path("/owner")
    @RolesAllowed({OWNER, FACILITY_MANAGER})
    public Response getBillDetails(@QueryParam("date") final String date,
                                   @QueryParam("apartmentId") final long apartmentId) {
        BillDto billDto = retry(() -> billEndpoint.getBillDetail(date, apartmentId), billEndpoint);
        return Response.ok().entity(billDto).build();
    }

    @GET
    @Path("/apartment/{id}")
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<ApartmentBillsDto> getBillsByApartmentId(@PathParam("id") final long apartmentId) {
        return billEndpoint.getBillsByApartmentId(apartmentId);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public BillDto getBillById(@PathParam("id") final long billId) {
        return billEndpoint.getBillById(billId);
    }
}
