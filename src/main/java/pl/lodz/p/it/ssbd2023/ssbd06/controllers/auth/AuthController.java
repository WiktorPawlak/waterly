package pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ErrorResponse;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.AccountIdentityStore;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.JwtProvider;


@Path(value = "/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private JwtProvider jwtProvider;
    @Inject
    private AccountIdentityStore accountIdentityStore;
    @Context
    private HttpServletRequest httpServletRequest;
    @Inject
    private AccountEndpoint accountEndpoint;

    @OnlyGuest
    @POST
    @Path("/login")
    public Response login(@NotNull @Valid final Credentials credentials) {
        Response response;
        CredentialValidationResult validationResult = accountIdentityStore.validate(credentials);

        if (validationResult.getStatus() != VALID) {
            accountEndpoint.saveFailedAuthAttempt(LocalDateTime.now(), credentials.getLogin());
            response = Response.status(UNAUTHORIZED)
                    .entity(new ErrorResponse("Wrong login or password"))
                    .build();
        } else {
            accountEndpoint.saveSuccessfulAuthAttempt(LocalDateTime.now(), credentials.getLogin(), httpServletRequest.getRemoteAddr());
            log.info("User " + credentials.getLogin() + " authenticated with IP " + httpServletRequest.getRemoteAddr());
            response = Response.ok()
                    .entity(jwtProvider.createToken(validationResult))
                    .build();
        }
        return response;
    }
}
