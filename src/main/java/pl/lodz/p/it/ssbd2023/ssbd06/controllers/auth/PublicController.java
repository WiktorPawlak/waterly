package pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AccountIdentityStore;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.Credentials;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.JwtProvider;

@Path(value = "/public")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicController {

    @Inject
    JwtProvider jwtProvider;
    @Inject
    AccountIdentityStore accountIdentityStore;

    @POST
    @Path("/login")
    public Response login(@Valid final Credentials credentials) {
        CredentialValidationResult validationResult = accountIdentityStore.validate(credentials);

        if (validationResult.getStatus() == VALID) {
            return Response.ok(jwtProvider.createToken(
                    validationResult.getCallerPrincipal().getName(),
                    validationResult.getCallerGroups()
            )).build();
        } else {
            return Response.status(UNAUTHORIZED).entity(new ErrorResponse("Wrong login or password")).build();
        }

    }
}
