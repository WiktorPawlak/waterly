package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;
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
    @ConfigProperty(name = "jwt.private.key.location")
    private String privateKeyLocation;

    public String createToken(final CredentialValidationResult validationResult) throws Exception {
        PrivateKey privateKey = readPrivateKey(privateKeyLocation);

        JwtClaimsBuilder claimsBuilder = Jwt.claims();
        long currentTimeInSecs = currentTimeInSecs();

        claimsBuilder.issuer(issuer);
        claimsBuilder.subject(validationResult.getCallerPrincipal().getName());
        claimsBuilder.issuedAt(currentTimeInSecs);
        claimsBuilder.expiresAt(currentTimeInSecs + expirationTime);
        claimsBuilder.groups(validationResult.getCallerGroups());

        return claimsBuilder.jws().signatureKeyId(privateKeyLocation).sign(privateKey);
    }

    public static PrivateKey readPrivateKey(final String pemResName) throws Exception {
        try (InputStream contentIS = JwtProvider.class.getResourceAsStream(pemResName)) {
            byte[] tmp = new byte[4096];
            int length = contentIS.read(tmp);
            return decodePrivateKey(new String(tmp, 0, length, "UTF-8"));
        }
    }

    public static PrivateKey decodePrivateKey(final String pemEncoded) throws Exception {
        byte[] encodedBytes = toEncodedBytes(pemEncoded);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    public static byte[] toEncodedBytes(final String pemEncoded) {
        final String normalizedPem = removeBeginEnd(pemEncoded);
        return Base64.getDecoder().decode(normalizedPem);
    }

    public static String removeBeginEnd(String pem) {
        pem = pem.replaceAll("-----BEGIN (.*)-----", "");
        pem = pem.replaceAll("-----END (.*)----", "");
        pem = pem.replaceAll("\r\n", "");
        pem = pem.replaceAll("\n", "");
        return pem.trim();
    }

    public static int currentTimeInSecs() {
        long currentTimeMS = System.currentTimeMillis();
        return (int) (currentTimeMS / 1000);
    }

}

