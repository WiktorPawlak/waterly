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
import jakarta.ws.rs.QueryParam;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;

@Path("/apartments")
@RequestScoped
public class ApartmentController extends RepeatableTransactionController {

    @GET
    public List<ApartmentsDto> getApartments() {
        throw new NotSupportedException();
    }

    @GET
    public List<ApartmentsDto> getApartmentsByOwnerId(@QueryParam("ownerId") final String ownerId) {
        throw new NotSupportedException();
    }

    @GET
    @Path("/{id}")
    public ApartmentDto getApartmentById(@PathParam("id") final long apartmentId) {
        throw new NotSupportedException();
    }

    @POST
    public void establishApartment(@NotNull @Valid final CreateApartmentDto dto) {
        throw new NotSupportedException();
    }

    @PUT
    public void updateApartmentDetails(@NotNull @Valid final EditApartmentDetailsDto dto) {
        throw new NotSupportedException();
    }

    @PUT
    @Path("/{id}")
    public void changeApartmentOwner(@PathParam("id") final long id, final ChangeApartmentOwnerDto dto) {
        throw new NotSupportedException();
    }

}
