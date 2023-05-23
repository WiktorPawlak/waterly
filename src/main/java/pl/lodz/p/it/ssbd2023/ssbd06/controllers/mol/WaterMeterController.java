package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mol;

import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

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
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AddMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMetersDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints.WaterMeterEndpoint;

@Log
@Path("/water-meters")
@RequestScoped
public class WaterMeterController extends RepeatableTransactionController {

    @Inject
    private WaterMeterEndpoint waterMeterEndpoint;

    @GET
    public List<WaterMetersDto> getWaterMeters() {
        throw new NotSupportedException();
    }

    @GET
    @Path("/apartment")
    public List<WaterMetersDto> getWaterMatersByApartmentId(@QueryParam("apartmentId") final long apartmentId) {
        throw new NotSupportedException();
    }

    @POST
    @Path("/water-meter-check")
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Response performWaterMeterCheck(@NotNull @Valid final WaterMeterCheckDto dto) {
        waterMeterEndpoint.performWaterMeterCheck(dto);
        return Response.status(NO_CONTENT).build();
    }

    @POST
    public void assignWaterMeterToLocal(@NotNull @Valid final AssignWaterMeterDto dto) {
        throw new NotSupportedException();
    }

    @PUT
    public void updateWaterMeter(@NotNull @Valid final UpdateWaterMeterDto dto) {
        throw new NotSupportedException();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed(FACILITY_MANAGER)
    public void disableWaterMeter(@PathParam("id") final long id) {
        waterMeterEndpoint.disableWaterMeter(id);
    }

    @POST
    @Path("{id}")
    public void replaceWaterMeter(@PathParam("id") final long id, @NotNull @Valid final ReplaceWaterMeterDto dto) {
        waterMeterEndpoint.replaceWaterMeter(id, dto);
    }

    @POST
    @Path("/main-water-meter")
    public void addMainWaterMeter(@NotNull @Valid final AddMainWaterMeterDto dto) {
        throw new NotSupportedException();
    }
}