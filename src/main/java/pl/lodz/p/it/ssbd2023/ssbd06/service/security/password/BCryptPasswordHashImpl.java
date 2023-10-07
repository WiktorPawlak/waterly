package pl.lodz.p.it.ssbd2023.ssbd06.service.security.password;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@PermitAll
public class BCryptPasswordHashImpl {

    @ConfigProperty(name = "password.bcrypt.round-count")
    int roundCount;

    public String generate(final char[] password) {
        return BCrypt.withDefaults().hashToString(roundCount, password);
    }

    public boolean verify(final char[] password, final String hashedPassword) {
        return BCrypt.verifyer().verify(password, hashedPassword).verified;
    }
}
