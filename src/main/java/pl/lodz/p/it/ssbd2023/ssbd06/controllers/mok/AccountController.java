package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mok;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints.AccountEndpoint;

@Path("/accounts")
public class AccountController {

    @Inject
    private AccountEndpoint accountEndpoint;

    @RolesAllowed("ADMINISTRATOR")
    @PUT
    @Path("/{id}/active")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeAccountActiveStatus(@PathParam("id") final long id, final boolean active) {
        accountEndpoint.changeAccountActiveStatus(id, active);
        return Response.ok().build();
    }
}
