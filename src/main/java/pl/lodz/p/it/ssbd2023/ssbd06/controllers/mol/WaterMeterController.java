package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterChecksDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.WaterMeterCheckEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.WaterMeterEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.EtagValidationFilter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadSigner;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Order;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Page;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.PageSize;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.WaterMetersOrderBy;

@Log
@Path("/water-meters")
@RequestScoped
public class WaterMeterController {

    @Inject
    private WaterMeterEndpoint waterMeterEndpoint;
    @Inject
    private WaterMeterCheckEndpoint waterMeterCheckEndpoint;

    @Inject
    private PayloadSigner payloadSigner;

    @RolesAllowed(FACILITY_MANAGER)
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWaterMeterById(@PathParam("id") final long id) {
        WaterMeterDto waterMeterDto = waterMeterEndpoint.getWaterMeterById(id);
        String entityTag = payloadSigner.sign(waterMeterDto);
        return Response.ok().entity(waterMeterDto).header("ETag", entityTag).build();
    }

    @GET
    @RolesAllowed(FACILITY_MANAGER)
    public Response getWaterMeters(@Page @QueryParam("page") final Integer page,
                                   @PageSize @QueryParam("pageSize") final Integer pageSize,
                                   @Order @QueryParam("order") final String order,
                                   @WaterMetersOrderBy @QueryParam("orderBy") final String orderBy,
                                   @QueryParam("pattern") final String pattern) {

        PaginatedList<WaterMeterDto> waterMeters = waterMeterEndpoint.getWaterMetersList(pattern, page, pageSize, order, orderBy);
        return Response.ok().entity(waterMeters).build();
    }

    @GET
    @Path("/apartment/{apartmentId}")
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Response getWaterMatersByApartmentId(@PathParam("apartmentId") final long apartmentId) {
        List<WaterMeterDto> waterMeters = (waterMeterEndpoint.getWaterMetersByApartmentId(apartmentId));
        return Response.ok().entity(waterMeters).build();
    }

    @POST
    @Path("/main-water-meter")
    @RolesAllowed(FACILITY_MANAGER)
    public Response createMainWaterMeter(@NotNull @Valid final CreateMainWaterMeterDto dto) {
        waterMeterEndpoint.createMainWaterMeter(dto);
        return Response.status(CREATED).build();
    }

    @POST
    @Path("/water-meter-checks")
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Response performWaterMeterChecks(@NotNull @Valid final WaterMeterChecksDto dto) {
        if (dto.isManagerAuthored()) {
            waterMeterCheckEndpoint.initializePerformWaterMeterChecksByFM(dto);
        } else {
            waterMeterCheckEndpoint.initializePerformWaterMeterChecksByOwner(dto);
        }
        return Response.status(NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/replace")
    @RolesAllowed(FACILITY_MANAGER)
    public Response replaceWaterMeter(@PathParam("id") final long waterMeterId, @NotNull @Valid final ReplaceWaterMeterDto dto) {
        waterMeterEndpoint.replaceWaterMeter(waterMeterId, dto);
        return Response.status(NO_CONTENT).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(FACILITY_MANAGER)
    @EtagValidationFilter
    public Response updateWaterMeter(@PathParam("id") final long waterMeterId, @NotNull @Valid final UpdateWaterMeterDto dto) {
        waterMeterEndpoint.updateWaterMeter(waterMeterId, dto);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed(FACILITY_MANAGER)
    @PUT
    @Path("/{id}/active")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeWaterMeterActiveStatus(@PathParam("id") final long waterMeterId, @NotNull @Valid final WaterMeterActiveStatusDto dto) {
        waterMeterEndpoint.changeWaterMeterActiveStatus(waterMeterId, dto);
        return Response.ok().build();
    }
}