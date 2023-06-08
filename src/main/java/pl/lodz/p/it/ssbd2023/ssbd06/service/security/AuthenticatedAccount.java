package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;

@Stateless
public class AuthenticatedAccount {

    @Inject
    private SecurityContext securityContext;

    public String getLogin() {
        return securityContext.getCallerPrincipal() == null ? "GUEST" : securityContext.getCallerPrincipal().getName();
    }

    public boolean isAdmin() {
        return securityContext.isCallerInRole(ADMINISTRATOR);
    }

    public boolean isFacilityManager() {
        return securityContext.isCallerInRole(FACILITY_MANAGER);
    }
}
