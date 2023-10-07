package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import static jakarta.ws.rs.Priorities.AUTHENTICATION;

import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.security.enterprise.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@OnlyGuest
@Interceptor
@Priority(AUTHENTICATION)
public class OnlyGuestInterceptor {

    @Resource
    private SecurityContext securityContext;

    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        if (securityContext != null && securityContext.getCallerPrincipal() != null) {
            throw ApplicationBaseException.forbiddenOperationException();
        }
        return context.proceed();
    }
}
