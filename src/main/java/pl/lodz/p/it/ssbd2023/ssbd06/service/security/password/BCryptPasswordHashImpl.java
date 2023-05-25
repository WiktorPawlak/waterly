package pl.lodz.p.it.ssbd2023.ssbd06.service.security.password;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Stateless
@PermitAll
@BCryptHash
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BCryptPasswordHashImpl implements PasswordHash {

    @Inject
    @Property("password.bcrypt.round-count")
    private int roundCount;

    @Override
    public String generate(final char[] password) {
        return BCrypt.withDefaults().hashToString(roundCount, password);
    }

    @Override
    public boolean verify(final char[] password, final String hashedPassword) {
        return BCrypt.verifyer().verify(password, hashedPassword).verified;
    }
}
