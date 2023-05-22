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
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AddMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMetersDto;

@Path("/water-meters")
@RequestScoped
public class WaterMeterController extends RepeatableTransactionController {

    @GET
    public List<WaterMetersDto> getWaterMeters() {
        throw new NotSupportedException();
    }

    @GET
    public List<WaterMetersDto> getWaterMatersByApartmentId(@QueryParam("apartmentId") final long apartmentId) {
        throw new NotSupportedException();
    }

    @POST
    @Path("/water-meter-check")
    public void performWaterMeterCheck(@NotNull @Valid final WaterMeterCheckDto dto) {
        throw new NotSupportedException();
    }

    @POST
    public void assignWaterMeterToLocal(@NotNull @Valid final AssignWaterMeterDto dto) {
        throw new NotSupportedException();
    }

    @PUT
    public void updateWaterMeter(@NotNull @Valid final UpdateWaterMeterDto dto) {
        throw new NotSupportedException();
    }

    @POST
    @Path("{id}")
    public void replaceWaterMeter(@PathParam("id") final long id, @NotNull @Valid final ReplaceWaterMeterDto dto) {
        throw new NotSupportedException();
    }

    @POST
    @Path("/main-water-meter")
    public void addMainWaterMeter(@NotNull @Valid final AddMainWaterMeterDto dto) {
        throw new NotSupportedException();
    }
}