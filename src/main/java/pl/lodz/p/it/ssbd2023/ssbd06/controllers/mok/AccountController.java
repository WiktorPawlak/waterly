package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mok;

import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;
import java.util.logging.Logger;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountSearchPreferencesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountWithRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.GetPagedAccountListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.AccountAlreadyExistException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;

@Path("/accounts")
public class AccountController {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private AccountEndpoint accountEndpoint;

    @RolesAllowed(ADMINISTRATOR)
    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON_PATCH_JSON)
    public Response changeAccountActiveStatus(@PathParam("id") final long id, @NotNull @Valid final AccountActiveStatusDto dto) {
        accountEndpoint.changeAccountActiveStatus(id, dto);
        return Response.ok().build();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    @PUT
    @Path("/self")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOwnAccountDetails(@NotNull @Valid final UpdateAccountDetailsDto dto) throws AccountAlreadyExistException {
        accountEndpoint.updateOwnAccountDetails(dto);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed(ADMINISTRATOR)
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccountDetails(@PathParam("id") final long id, @NotNull @Valid final UpdateAccountDetailsDto dto)
            throws AccountAlreadyExistException {
        accountEndpoint.updateAccountDetails(id, dto);
        return Response.status(NO_CONTENT).build();
    }

    @PermitAll
    @PUT
    @Path("/account-details/{id}/accept")
    public Response acceptAccountDetailsUpdate(@PathParam("id") final long id) {
        accountEndpoint.acceptAccountDetailsUpdate(id);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    @POST
    @Path("self/account-details/resend-accept-email")
    public Response resendEmailToAcceptAccountDetailsUpdate() {
        accountEndpoint.resendEmailToAcceptAccountDetailsUpdate();
        return Response.status(OK).build();
    }

    @RolesAllowed(ADMINISTRATOR)
    @PUT
    @Path("/{id}/roles")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editAccountRoles(@PathParam("id") final long id, @NotNull @Valid final EditAccountRolesDto dto)
            throws ApplicationBaseException {
        accountEndpoint.editAccountRoles(id, dto);
        return Response.ok().build();
    }

    @PUT
    @Path("/self/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeOwnPassword(@NotNull @Valid final AccountPasswordDto dto)
            throws ApplicationBaseException {
        accountEndpoint.changeOwnAccountPassword(dto);
        return Response.ok().build();
    }

    @OnlyGuest
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAccount(@NotNull @Valid final AccountDto account) {
        accountEndpoint.registerUser(account);
        log.info(() -> "Registering account: " + account);
        return Response.ok().build();
    }

    @OnlyGuest
    @POST
    @Path("/{id}/resendVerificationToken")
    public Response resendVerificationToken(@PathParam("id") final long id) throws ApplicationBaseException {
        accountEndpoint.resendVerificationToken(id);
        log.info(() -> "Resending verification token for account with id: " + id);
        return Response.ok().build();
    }

    @OnlyGuest
    @PUT
    @Path("/confirm-registration")
    public Response confirmRegistration(@NotNull @QueryParam("token") final String token) throws ApplicationBaseException {
        accountEndpoint.confirmRegistration(token);
        log.info(() -> "Confirming account with token: " + token);
        return Response.ok().build();
    }

    @RolesAllowed({ADMINISTRATOR})
    @GET
    public Response getAccounts() throws ApplicationBaseException {
        List<AccountDto> accounts = accountEndpoint.getAccounts();
        return Response.ok().entity(accounts).build();
    }


    @RolesAllowed({ADMINISTRATOR})
    @POST
    @Path("/list")
    public Response getAccountsWithPagination(@NotNull @Valid final GetPagedAccountListDto dto) throws ApplicationBaseException {
        PaginatedList<AccountWithRolesDto> accounts = accountEndpoint.getAccountsList(dto);
        return Response.ok().entity(accounts).build();
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
    @GET
    @Path("/self/preferences")
    public Response getAccountSearchPreferences() throws ApplicationBaseException {
        AccountSearchPreferencesDto dto = accountEndpoint.getAccountsSearchPreferences();
        return Response.ok().entity(dto).build();
    }

    @POST
    @Path("/password/request-reset")
    public Response requestPasswordReset(@Email @QueryParam("email") final String email) {
        accountEndpoint.sendResetPasswordToken(email);
        log.info(() -> "Requested password reset by email: " + email);
        return Response.ok().build();
    }

    @POST
    @Path("/password/reset")
    @Consumes(MediaType.APPLICATION_JSON)
    public void resetPassword(@Valid final PasswordResetDto dto) throws TokenNotFoundException {
        accountEndpoint.resetPassword(dto);
    }

    @GET
    @Path("/self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOwnAccountDetails() {
        AccountDto accountDto = accountEndpoint.retrieveOwnAccountDetails();
        return Response.ok().entity(accountDto).build();
    }
}
