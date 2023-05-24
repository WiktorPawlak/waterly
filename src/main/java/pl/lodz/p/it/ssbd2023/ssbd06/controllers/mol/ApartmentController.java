package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.ApartmentEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;

@Path("/apartments")
@RequestScoped
public class ApartmentController extends RepeatableTransactionController {

    @Inject
    private ApartmentEndpoint apartmentEndpoint;

    @GET
    @RolesAllowed(FACILITY_MANAGER)
    public Response getApartments() {
        List<ApartmentsDto> apartments = apartmentEndpoint.getOwnerAllAccounts();
        return Response.ok().entity(apartments).build();
    }

    @GET
    @RolesAllowed(FACILITY_MANAGER)
    @Path("/owner")
    public Response getApartmentsByOwnerId(@QueryParam("ownerId") final long ownerId) {
        List<ApartmentsDto> apartments = apartmentEndpoint.getOwnerAllAccounts(ownerId);
        return Response.ok().entity(apartments).build();
    }

    @GET
    @RolesAllowed(FACILITY_MANAGER)
    @Path("/{id}")
    public Response getApartmentById(@PathParam("id") final long apartmentId) {
        Apartment apartment = apartmentEndpoint.getApartmentById(apartmentId);
        return Response.ok().entity(apartment).build();
    }

    @POST
    @RolesAllowed(FACILITY_MANAGER)
    public Response createApartment(@NotNull @Valid final CreateApartmentDto dto) {
        apartmentEndpoint.createApartment(dto);
        return Response.status(CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(FACILITY_MANAGER)
    public Response updateApartment(@PathParam("id") final long id, @NotNull @Valid final EditApartmentDetailsDto dto) {
        apartmentEndpoint.updateApartment(id, dto);
        return Response.status(NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}/owner")
    public void changeApartmentOwner(@PathParam("id") final long id, final ChangeApartmentOwnerDto dto) {
        throw new NotSupportedException();
    }

}
