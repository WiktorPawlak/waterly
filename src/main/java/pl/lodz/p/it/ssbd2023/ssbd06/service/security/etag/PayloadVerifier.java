package pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Log
@ApplicationScoped
public class PayloadVerifier {

    @Inject
    @Property("etag.key")
    private String key;

    private JWSVerifier verifier;

    @PostConstruct
    public void initialize() {
        try {
            verifier = new MACVerifier(key);
        } catch (final JOSEException e) {
            log.severe(() -> "Error while processing JWS:" + e.getCause());
            throw ApplicationBaseException.jwsException();
        }
    }

    public boolean verify(final String message) {
        try {
            final JWSObject jwsObject = JWSObject.parse(message);
            return jwsObject.verify(verifier);
        } catch (final JOSEException | ParseException e) {
            log.severe(() -> "Error while processing JWS:" + e.getCause());
            throw ApplicationBaseException.jwsException();
        }
    }
}