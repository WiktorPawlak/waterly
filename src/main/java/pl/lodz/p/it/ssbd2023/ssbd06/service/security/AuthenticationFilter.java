package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

import java.util.regex.Pattern;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@DeclareRoles({"ADMINISTRATOR", "OWNER", "FACILITY_MANAGER"})
public class AuthenticationFilter implements HttpAuthenticationMechanism {

    @Inject
    private JwtProvider jwtProvider;
    private final Pattern tokenPattern = Pattern.compile("^Bearer *([^ ]+) *$", Pattern.CASE_INSENSITIVE);

    @Override
    public AuthenticationStatus validateRequest(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final HttpMessageContext httpMessageContext
    ) throws AuthenticationException {

        String token = httpServletRequest.getHeader(AUTHORIZATION);
        if (validateToken(token)) {
            try {
                SimpleJWT jwt = jwtProvider.parse(getTokenValue(token));

                return httpMessageContext.notifyContainerAboutLogin(jwt.login(), jwt.roles());
            } catch (final Exception e) {
                log.error("Could not set user authentication in security context: " + e.getMessage());
                throw new AuthenticationException("Could not set user authentication in security context: " + e.getMessage());
            }
        } else if (!httpMessageContext.isProtected()) {
            return httpMessageContext.doNothing();
        }

        return httpMessageContext.responseUnauthorized();
    }

    private boolean validateToken(final String token) {
        return token != null && tokenPattern.matcher(token).matches();
    }

    private String getTokenValue(final String token) {
        return token.split(" ")[1];
    }

}
