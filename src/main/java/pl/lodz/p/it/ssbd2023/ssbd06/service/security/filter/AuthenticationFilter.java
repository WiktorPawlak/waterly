package pl.lodz.p.it.ssbd2023.ssbd06.service.security.filter;

import static jakarta.security.enterprise.AuthenticationStatus.SEND_FAILURE;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.JwtProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.SimpleJWT;

@RequestScoped
@DeclareRoles({ADMINISTRATOR, OWNER, FACILITY_MANAGER})
public class AuthenticationFilter implements HttpAuthenticationMechanism {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private JwtProvider jwtProvider;
    private final Pattern tokenPattern = Pattern.compile("^Bearer *([^ ]+) *$", Pattern.CASE_INSENSITIVE);

    @Override
    public AuthenticationStatus validateRequest(
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse,
            final HttpMessageContext httpMessageContext
    ) {

        String token = httpServletRequest.getHeader(AUTHORIZATION);
        if (validateToken(token)) {
            try {
                SimpleJWT jwt = jwtProvider.parse(getTokenValue(token));

                return httpMessageContext.notifyContainerAboutLogin(jwt.login(), jwt.roles());
            } catch (final Exception e) {
                log.severe(() -> "Could not set user authentication in security context: " + e.getMessage());
            }
        } else if (!httpMessageContext.isProtected()) {
            return httpMessageContext.doNothing();
        }

        return responseUnauthorized(httpServletResponse);
    }

    private boolean validateToken(final String token) {
        return token != null && tokenPattern.matcher(token).matches();
    }

    private String getTokenValue(final String token) {
        return token.split(" ")[1];
    }

    private AuthenticationStatus responseUnauthorized(final HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(SC_UNAUTHORIZED);
        return SEND_FAILURE;
    }

}
