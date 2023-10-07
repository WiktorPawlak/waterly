package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentBillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.OwnerBillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.BillEndpoint;

@Path("/bills")
@RequestScoped
@Transactional
public class BillController {

    @Inject
    private BillEndpoint billEndpoint;

    @GET
    @Path("/facility-manager")
    @RolesAllowed({FACILITY_MANAGER})
    public Response getBillDetails(@QueryParam("date") final String date,
                                   @QueryParam("apartmentId") final long apartmentId) {
        BillDto billDto = billEndpoint.getBillDetail(date, apartmentId);
        return Response.ok().entity(billDto).build();
    }

    @GET
    @Path("/owner")
    @RolesAllowed({OWNER})
    public Response getBillDetailsByOwner(@QueryParam("date") final String date,
                                          @QueryParam("apartmentId") final long apartmentId) {
        BillDto billDto = billEndpoint.getBillDetailByOwner(date, apartmentId);
        return Response.ok().entity(billDto).build();
    }

    @GET
    @Path("/apartment/{id}")
    @RolesAllowed({OWNER, FACILITY_MANAGER})
    public List<ApartmentBillDto> getBillsByApartmentId(@PathParam("id") final long apartmentId) {
        return billEndpoint.getBillsByApartmentId(apartmentId);
    }

    @GET
    @Path("/owner/{login}")
    @RolesAllowed(OWNER)
    public List<OwnerBillDto> getOwnerBills(@PathParam("login") final String login) {
        return billEndpoint.getOwnerBills(login);
    }
}
