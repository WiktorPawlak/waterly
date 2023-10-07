package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.security.enterprise.SecurityContext;

@RequestScoped
public class AuthenticatedAccount {

    @Resource
    private SecurityContext securityContext;

    public String getLogin() {
        return securityContext.getCallerPrincipal() == null ? "GUEST" : securityContext.getCallerPrincipal().getName();
    }
}
