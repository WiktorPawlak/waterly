package pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;

import java.time.LocalDateTime;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.spi.HttpRequest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints.AccountEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.AccountIdentityStore;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.Credentials;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.JwtProvider;

@Log
@Path(value = "/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Transactional
public class AuthController {

    @Inject
    private JwtProvider jwtProvider;
    @Inject
    private AccountIdentityStore accountIdentityStore;
    @Context
    private HttpRequest httpServletRequest;
    @Inject
    private AccountEndpoint accountEndpoint;

    @ConfigProperty(name = "ip.resolve.strategy")
    private String ipResolvingStrategy;

    @ConfigProperty(name = "real.ip.header.name")
    private String realIpHeaderName;

    @OnlyGuest
    @POST
    @Path("/login")
    public Response login(@NotNull @Valid final Credentials credentials) throws Exception {
        CredentialValidationResult validationResult = accountIdentityStore.validate(credentials);

        if (validationResult.getStatus() != VALID) {
            accountEndpoint.saveFailedAuthAttempt(LocalDateTime.now(), credentials.getLogin());
            throw ApplicationBaseException.authenticationException();
        }

        Account account = accountEndpoint.findByLogin(credentials.getLogin());

        if (account.isTwoFAEnabled()) {
            if (credentials.getTwoFACode() == null) {
                accountEndpoint.requestForTwoFACode(credentials.getLogin());
                throw ApplicationBaseException.twoFARequestedException();
            } else if (!accountEndpoint.verifyOTP(account, credentials.getTwoFACode())) {
                accountEndpoint.saveFailedAuthAttempt(LocalDateTime.now(), credentials.getLogin());
                throw ApplicationBaseException.authenticationException();
            }
        }

        String authenticationIpAddress = "";
        if ("header".equals(ipResolvingStrategy)) {
            authenticationIpAddress = httpServletRequest.getHttpHeaders().getHeaderString(realIpHeaderName);
        } else {
            authenticationIpAddress = httpServletRequest.getRemoteAddress();
        }

        accountEndpoint.saveSuccessfulAuthAttempt(LocalDateTime.now(), credentials.getLogin(), authenticationIpAddress);
        log.info("User " + credentials.getLogin() + " authenticated with IP " + authenticationIpAddress);

        return Response.ok()
                .entity(jwtProvider.createToken(validationResult))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
