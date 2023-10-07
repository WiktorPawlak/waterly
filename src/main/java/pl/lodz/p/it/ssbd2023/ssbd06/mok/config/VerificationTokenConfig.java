package pl.lodz.p.it.ssbd2023.ssbd06.mok.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@ApplicationScoped
public class VerificationTokenConfig {

    private static final double HALF = .5;

    @Getter
    @ConfigProperty(name = "verification.token.expirationTimeInMinutes")
    private Integer expirationTimeInMinutes;

    @Getter
    @ConfigProperty(name = "reset.token.expirationTimeInMinutes")
    private Integer expirationResetTimeInMinutes;

    @Getter
    @ConfigProperty(name = "changePassword.token.expirationTimeInMinutes")
    private Integer expirationChangePasswordTimeInMinutes;

    @Getter
    @ConfigProperty(name = "accountDetails.token.expirationTimeInMinutes")
    private Integer expirationAccountDetailsInMinutes;

    public double getHalfExpirationTimeInMinutes() {
        return expirationTimeInMinutes * HALF;
    }
}
