package pl.lodz.p.it.ssbd2023.ssbd06.service.security.etag;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@ApplicationScoped
@Log
public class PayloadSigner {

    @Inject
    @Property("etag.key")
    private String key;

    @Inject
    @Property("etag.algorithm")
    private String algorithm;

    private JWSSigner signer;

    @PostConstruct
    public void initialize() {
        try {
            signer = new MACSigner(key);
        } catch (final KeyLengthException e) {
            log.severe(() -> "Error while processing JWS:" + e.getCause());
            throw ApplicationBaseException.jwsException();
        }
    }

    public String sign(final Signable signable) {
        try {
            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.parse(algorithm)), new Payload(signable.createPayload()));
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (final JOSEException e) {
            log.severe(() -> "Error while processing JWS:" + e.getCause());
            throw ApplicationBaseException.jwsException();
        }
    }
}