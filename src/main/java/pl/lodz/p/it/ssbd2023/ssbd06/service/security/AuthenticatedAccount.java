package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AuthenticatedAccount {

    @Inject
    private SecurityIdentity securityContext;

    public String getLogin() {
        return securityContext.getPrincipal() == null ? "GUEST" : securityContext.getPrincipal().getName();
    }
}
