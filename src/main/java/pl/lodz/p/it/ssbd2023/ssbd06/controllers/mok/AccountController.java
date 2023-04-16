package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mok;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.IdenticalPasswordsException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.UnmatchedPasswordsException;

@Path("/accounts")
public class AccountController {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

    @PUT
    @Path("/self/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeOwnPassword(@NotNull final AccountPasswordDto accountPasswordDto)
            throws IdenticalPasswordsException, UnmatchedPasswordsException {
        Set<ConstraintViolation<AccountPasswordDto>> violation = validator.validate(accountPasswordDto);
        List<String> errors = violation.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        if (!violation.isEmpty()) {
            return Response.status(BAD_REQUEST).entity(errors).build();
        }
        accountEndpoint.changeOwnAccountPassword(accountPasswordDto);
        return Response.ok().build();
    }
}
