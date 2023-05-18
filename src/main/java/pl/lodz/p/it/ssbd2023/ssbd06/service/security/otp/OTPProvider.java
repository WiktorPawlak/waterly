package pl.lodz.p.it.ssbd2023.ssbd06.service.security.otp;

import java.time.LocalDateTime;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.SecretGenerator;
import com.bastiaanjansen.otp.TOTPGenerator;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TwoFactorAuthentication;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@RequestScoped
public class OTPProvider {
    private static final int PASSWORD_LENGTH = 8;

    @Inject
    @Property("otp.duration.seconds")
    private int otpDuration;

    @Inject
    @BCryptHash
    private PasswordHash hashProvider;


    public TOTPGenerator createTOTGenerator(final String secret) {
        byte[] secretKey = SecretGenerator.generate();
        return new TOTPGenerator.Builder(secretKey)
                .withHOTPGenerator(builder -> {
                    builder.withPasswordLength(PASSWORD_LENGTH);
                    builder.withAlgorithm(HMACAlgorithm.SHA256);
                })
                .build();
    }

    public Tuple2<TwoFactorAuthentication, String> generateOTPPassword(final Account account) {
        String otp = createTOTGenerator(account.getOtpSecret()).now();
        TwoFactorAuthentication twoFa = new TwoFactorAuthentication(hashProvider.generate(otp.toCharArray()), LocalDateTime.now(), account);
        return Tuple.of(twoFa, otp);
    }

    public boolean verifyOTP(final String code, final TwoFactorAuthentication twoFA) {
        if (LocalDateTime.now().isAfter(twoFA.getTokenCreationDate().plusSeconds(otpDuration))) {
            throw ApplicationBaseException.invalidOTPException();
        } else {
            return hashProvider.verify(code.toCharArray(), twoFA.getToken());
        }
    }

}
