package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionProcessor;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.ApartmentEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.WaterMeterEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.EtagValidationFilter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadSigner;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.ApartmentOrderBy;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Order;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Page;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PageSize;

@Path("/apartments")
@RequestScoped
public class ApartmentController extends RepeatableTransactionProcessor {

    @Inject
    private ApartmentEndpoint apartmentEndpoint;

    @Inject
    private WaterMeterEndpoint waterMeterEndpoint;

    @Inject
    private PayloadSigner payloadSigner;

    @GET
    @RolesAllowed(FACILITY_MANAGER)
    public Response getApartments(
            @Page @QueryParam("page") final Integer page,
            @PageSize @QueryParam("pageSize") final Integer pageSize,
            @Order @QueryParam("order") final String order,
            @ApartmentOrderBy @QueryParam("orderBy") final String orderBy,
            @QueryParam("pattern") final String pattern
    ) {
        PaginatedList<ApartmentDto> apartments = retry(() -> apartmentEndpoint.getAllApartments(pattern, page, pageSize, order, orderBy), apartmentEndpoint);
        return Response.ok().entity(apartments).build();
    }

    @GET
    @RolesAllowed(OWNER)
    @Path("/self")
    public Response getSelfApartments(
            @Page @QueryParam("page") final Integer page,
            @PageSize @QueryParam("pageSize") final Integer pageSize,
            @Order @QueryParam("order") final String order,
            @ApartmentOrderBy @QueryParam("orderBy") final String orderBy,
            @QueryParam("pattern") final String pattern
    ) {
        PaginatedList<ApartmentDto> apartments = retry(() -> apartmentEndpoint.getSelfApartments(pattern, page, pageSize, order, orderBy), apartmentEndpoint);
        return Response.ok().entity(apartments).build();
    }

    @GET
    @RolesAllowed(FACILITY_MANAGER)
    @Path("/{id}")
    public Response getApartmentById(@PathParam("id") final long apartmentId) {
        ApartmentDto apartment = apartmentEndpoint.getApartmentById(apartmentId);
        return Response.ok().entity(apartment).header("ETag", payloadSigner.sign(apartment)).build();
    }

    @POST
    @RolesAllowed(FACILITY_MANAGER)
    public Response createApartment(@NotNull @Valid final CreateApartmentDto dto) {
        retry(() -> apartmentEndpoint.createApartment(dto), apartmentEndpoint);
        return Response.status(CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(FACILITY_MANAGER)
    public Response updateApartment(@PathParam("id") final long apartmentId, @NotNull @Valid final EditApartmentDetailsDto dto) {
        retry(() -> apartmentEndpoint.updateApartment(apartmentId, dto), apartmentEndpoint);
        return Response.status(NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}/owner")
    @EtagValidationFilter
    @RolesAllowed(FACILITY_MANAGER)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeApartmentOwner(@PathParam("id") final long apartmentId, @NotNull @Valid final ChangeApartmentOwnerDto dto) {
        retry(() -> apartmentEndpoint.changeApartmentOwner(apartmentId, dto), apartmentEndpoint);
        return Response.ok().build();
    }

    @POST
    @Path("/{id}/water-meter")
    @RolesAllowed(FACILITY_MANAGER)
    public Response assignWaterMeterToApartment(@PathParam("id") final long apartmentId, @NotNull @Valid final AssignWaterMeterDto dto) {
        retry(() -> waterMeterEndpoint.addWaterMeter(apartmentId, dto), waterMeterEndpoint);
        return Response.status(CREATED).build();
    }

}
