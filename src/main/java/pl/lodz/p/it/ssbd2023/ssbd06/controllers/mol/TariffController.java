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
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionController;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AddTariffDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.TariffsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateTariffDto;

@Path("/tariffs")
@RequestScoped
public class TariffController extends RepeatableTransactionController {

    @GET
    public List<TariffsDto> getTariffs() {
        throw new NotSupportedException();
    }

    @PUT
    public void updateTariff(@NotNull @Valid final UpdateTariffDto dto) {
        throw new NotSupportedException();
    }

    @POST
    public void addTariff(@NotNull @Valid final AddTariffDto dto) {
        throw new NotSupportedException();
    }

}
