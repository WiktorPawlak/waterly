package pl.lodz.p.it.ssbd2023.ssbd06.controllers.mok;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountSearchPreferencesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreatedAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.GetPagedAccountListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.ListAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordChangeByAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.ReCAPTCHA;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.EtagValidationFilter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag.PayloadSigner;
import pl.lodz.p.it.ssbd2023.ssbd06.service.validators.Email;

@Log
@Path("/accounts")
public class AccountController {

    @Inject
    private AccountEndpoint accountEndpoint;

    @Inject
    private PayloadSigner payloadSigner;

    @Inject
    private ReCAPTCHA recaptchaVerifier;

    @RolesAllowed(ADMINISTRATOR)
    @PUT
    @Path("/{id}/active")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeAccountActiveStatus(@PathParam("id") final long id, @NotNull @Valid final AccountActiveStatusDto dto) {
        accountEndpoint.changeAccountActiveStatus(id, dto);
        return Response.ok().build();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    @PUT
    @Path("/self")
    @Consumes(MediaType.APPLICATION_JSON)
    @EtagValidationFilter
    public Response editOwnAccountDetails(@NotNull @Valid final EditAccountDetailsDto dto) throws ApplicationBaseException {
        accountEndpoint.editOwnAccountDetails(dto);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed(ADMINISTRATOR)
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @EtagValidationFilter
    public Response editAccountDetails(@PathParam("id") final long id, @NotNull @Valid final EditAccountDetailsDto dto)
            throws ApplicationBaseException {
        accountEndpoint.editAccountDetails(id, dto);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed(ADMINISTRATOR)
    @PUT
    @Path("/{id}/email")
    @Consumes(MediaType.APPLICATION_JSON)
    @EtagValidationFilter
    public Response editEmail(@PathParam("id") final long id, @NotNull @Valid final EditEmailDto dto) throws ApplicationBaseException {
        accountEndpoint.editEmail(id, dto);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    @PUT
    @Path("/self/email")
    @Consumes(MediaType.APPLICATION_JSON)
    @EtagValidationFilter
    public Response editOwnEmail(@NotNull @Valid final EditEmailDto dto) throws ApplicationBaseException {
        accountEndpoint.editOwnEmail(dto);
        return Response.status(NO_CONTENT).build();
    }

    @PermitAll
    @POST
    @Path("/email/accept")
    public Response acceptEmailUpdate(@NotNull @QueryParam("token") final String token) {
        accountEndpoint.acceptEmailUpdate(token);
        return Response.status(NO_CONTENT).build();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    @POST
    @Path("self/email/resend-accept-email")
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

    @OnlyGuest
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAccount(@NotNull @Valid final CreateAccountDto account,
                                    final @NotNull @Valid @QueryParam("recaptchaResponse") String recaptchaResponse) {
        CreatedAccountDto createdAccountDto = accountEndpoint.registerUser(account, recaptchaResponse);
        return Response.status(Response.Status.CREATED).entity(createdAccountDto).build();
    }


    @RolesAllowed(ADMINISTRATOR)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(@NotNull @Valid final CreateAccountDto account) {
        accountEndpoint.createAccount(account);
        log.info(() -> "Creating account: " + account);
        return Response.status(CREATED).build();
    }

    @OnlyGuest
    @POST
    @Path("/{id}/resend-verification-token")
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

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
    @GET
    @Path("/self/preferences")
    public Response getAccountSearchPreferences() throws ApplicationBaseException {
        AccountSearchPreferencesDto dto = accountEndpoint.getAccountsSearchPreferences();
        return Response.ok().entity(dto).build();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    @PUT
    @Path("/self/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeOwnPassword(@NotNull @Valid final AccountPasswordDto dto)
            throws ApplicationBaseException {
        accountEndpoint.changeOwnAccountPassword(dto);
        return Response.ok().build();
    }

    @RolesAllowed({ADMINISTRATOR})
    @POST
    @Path("/password/request-change")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response requestPasswordChange(@Valid @Email @QueryParam("email") final String email, final PasswordChangeByAdminDto dto) {
        accountEndpoint.sendChangePasswordToken(email, dto);
        log.info(() -> "Requested password change for user with email: " + email);
        return Response.ok().build();
    }

    @OnlyGuest
    @POST
    @Path("/password/request-reset")
    public Response requestPasswordReset(@Valid @Email @QueryParam("email") final String email) {
        accountEndpoint.sendResetPasswordToken(email);
        log.info(() -> "Requested password reset by email: " + email);
        return Response.ok().build();
    }

    @PermitAll
    @POST
    @Path("/password/reset")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(@NotNull @Valid final PasswordResetDto dto) throws TokenNotFoundException {
        accountEndpoint.resetPassword(dto);
        return Response.ok().build();
    }

    @RolesAllowed(FACILITY_MANAGER)
    @DELETE
    @Path("/{id}/reject")
    public Response rejectOwnerAccount(@PathParam("id") final long id) {
        accountEndpoint.rejectOwnerAccount(id);
        return Response.ok().build();
    }

    @RolesAllowed(FACILITY_MANAGER)
    @POST
    @Path("/{id}/accept")
    public Response acceptOwnerAccount(@PathParam("id") final long id) {
        accountEndpoint.acceptOwnerAccount(id);
        return Response.ok().build();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    @GET
    @Path("/self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOwnAccountDetails() {
        AccountDto accountDto = accountEndpoint.retrieveOwnAccountDetails();
        String entityTag = payloadSigner.sign(accountDto);
        return Response.ok().entity(accountDto).header("ETag", entityTag).build();
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER})
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountById(@PathParam("id") final long id) {
        AccountDto accountDto = accountEndpoint.getUserById(id);
        String entityTag = payloadSigner.sign(accountDto);
        return Response.ok().entity(accountDto).header("ETag", entityTag).build();
    }

    @RolesAllowed(FACILITY_MANAGER)
    @POST
    @Path("/to-verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotConfirmedAccounts(@NotNull @Valid final GetPagedAccountListDto dto, @QueryParam("pattern") final String pattern) {
        PaginatedList<ListAccountDto> accounts = accountEndpoint.getNotConfirmedAccounts(pattern, dto);
        return Response.ok().entity(accounts).build();
    }

    @RolesAllowed(FACILITY_MANAGER)
    @GET
    @Path("/owners")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOwnersAccounts() {
        List<ListAccountDto> accounts = accountEndpoint.getOwnersAccounts();
        return Response.ok().entity(accounts).build();
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER})
    @POST
    @Path("/list")
    public Response getAccountsWithPagination(@NotNull @Valid final GetPagedAccountListDto dto, @QueryParam("pattern") final String pattern)
            throws ApplicationBaseException {
        PaginatedList<ListAccountDto> accounts = accountEndpoint.getAccountsList(pattern, dto);
        return Response.ok().entity(accounts).build();
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER})
    @GET
    @Path("/list/name-suggestions")
    public Response getNameSuggestions(@QueryParam("pattern") final String pattern)
            throws ApplicationBaseException {
        List<String> names = accountEndpoint.getNameSuggestions(pattern);
        return Response.ok().entity(names).build();
    }
}
