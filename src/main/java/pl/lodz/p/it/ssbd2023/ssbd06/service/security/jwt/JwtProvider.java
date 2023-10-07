package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.enterprise.context.RequestScoped;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import lombok.RequiredArgsConstructor;

@RequestScoped
@RequiredArgsConstructor
public class JwtProvider {

    private static final String ROLES_CLAIM_NAME = "roles";
    @ConfigProperty(name = "jwt.issuer")
    private String issuer;
    @ConfigProperty(name = "jwt.expiration.time")
    private int expirationTime;
    @ConfigProperty(name = "jwt.key")
    private String jwtKey;

    public String createToken(final CredentialValidationResult validationResult) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuedAt(now)
                .withIssuer(issuer)
                .withJWTId(validationResult.getCallerPrincipal().getName())
                .withClaim(ROLES_CLAIM_NAME, new ArrayList<>(validationResult.getCallerGroups()))
                .withExpiresAt(now.plusSeconds(expirationTime))
                .sign(signingAlgorithm());
    }

    public SimpleJWT parse(final String jwtText) {

        JWTVerifier verifier = JWT.require(signingAlgorithm()).withIssuer(issuer).build();
        final DecodedJWT jwt = verifier.verify(jwtText);
        final Claim roles = jwt.getClaim(ROLES_CLAIM_NAME);

        return new SimpleJWT(jwt.getId(), new HashSet<>(roles.asList(String.class)));
    }

    private Algorithm signingAlgorithm() {
        return Algorithm.HMAC256(jwtKey);
    }
}

