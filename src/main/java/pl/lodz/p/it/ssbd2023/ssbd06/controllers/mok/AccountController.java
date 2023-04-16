package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mok;

import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
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

    @RolesAllowed({"OWNER", "FACILITY_MANAGER", "ADMINISTRATOR"})
    @PUT
    @Path("/self")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOwnAccountDetails(@Valid @NotNull final UpdateAccountDetailsDto updateAccountDetailsDto) {
        accountEndpoint.updateOwnAccountDetails(updateAccountDetailsDto);
        return Response.status(NO_CONTENT).build();
    }

    @PUT
    @Path("/account-details/{id}/accept")
    public Response acceptAccountDetailsUpdate(@PathParam("id") final long id) {
        accountEndpoint.acceptAccountDetailsUpdate(id);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed({"OWNER", "FACILITY_MANAGER", "ADMINISTRATOR"})
    @POST
    @Path("self/account-details/resend-accept-email")
    public Response resendEmailToAcceptAccountDetailsUpdate() {
        accountEndpoint.resendEmailToAcceptAccountDetailsUpdate();
        return Response.status(OK).build();
    }

    @RolesAllowed("ADMINISTRATOR")
    @PUT
    @Path("/{id}/roles")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoleToAccount(@PathParam("id") final long id, @QueryParam("role") final String role) {
        accountEndpoint.addRoleToAccount(id, role.toUpperCase());
        return Response.ok().build();
    }
}
