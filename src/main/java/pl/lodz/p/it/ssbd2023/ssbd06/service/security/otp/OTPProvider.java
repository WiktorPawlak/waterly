package pl.lodz.p.it.ssbd2023.ssbd06.service.security.otp;

import java.time.Duration;
import java.time.LocalDateTime;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTPGenerator;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.TwoFactorAuthenticationFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TwoFactorAuthentication;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@Stateless
@Monitored
public class OTPProvider {
    private static final int PASSWORD_LENGTH = 8;

    @Inject
    @Property("otp.duration.seconds")
    private int otpDuration;

    @Inject
    private TwoFactorAuthenticationFacade twoFactorAuthenticationFacade;

    @Inject
    @BCryptHash
    private PasswordHash hashProvider;


    public TOTPGenerator createTOTGenerator(final String secret) {
        byte[] secretKey = secret.getBytes();

        return new TOTPGenerator.Builder(secretKey)
                .withHOTPGenerator(builder -> {
                    builder.withPasswordLength(PASSWORD_LENGTH);
                    builder.withAlgorithm(HMACAlgorithm.SHA256);
                })
                .withPeriod(Duration.ofSeconds(otpDuration))
                .build();
    }

    public String generateOTPPassword(final Account account) {
        String otp = createTOTGenerator(account.getOtpSecret()).now();
        TwoFactorAuthentication twoFa = new TwoFactorAuthentication(hashProvider.generate(otp.toCharArray()), LocalDateTime.now(), account);
        twoFactorAuthenticationFacade.create(twoFa);
        return otp;
    }

    public boolean verifyOTP(final Account account, final String code) {
        TwoFactorAuthentication twoFA = twoFactorAuthenticationFacade.findByAccountId(account.getId());
        if (twoFA.getTokenCreationDate().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("sth");
        } else {
            return twoFA.getToken().equals(hashProvider.generate(code.toCharArray()));
        }
    }

}
