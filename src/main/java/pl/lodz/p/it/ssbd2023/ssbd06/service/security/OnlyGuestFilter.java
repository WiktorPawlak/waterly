package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import static jakarta.ws.rs.Priorities.AUTHENTICATION;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@OnlyGuest
@Provider
@Priority(AUTHENTICATION)
public class OnlyGuestFilter implements ContainerRequestFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        SecurityContext securityContext = requestContext.getSecurityContext();
        if (securityContext != null && securityContext.getUserPrincipal() != null) {
            throw new ForbiddenException();
        }
    }
}

