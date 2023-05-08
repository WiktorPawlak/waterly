package pl.lodz.p.it.ssbd2023.ssbd06.mok.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@ApplicationScoped
public class VerificationTokenConfig {

    private static final double HALF = .5;

    @Getter
    @Inject
    @Property("verification.token.expirationTimeInMinutes")
    private Integer expirationTimeInMinutes;

    @Getter
    @Inject
    @Property("reset.token.expirationTimeInMinutes")
    private Integer expirationResetTimeInMinutes;

    @Getter
    @Inject
    @Property("accountDetails.token.expirationTimeInMinutes")
    private Integer expirationAccountDetailsInMinutes;

    public double getHalfExpirationTimeInMinutes() {
        return expirationTimeInMinutes * HALF;
    }
}
