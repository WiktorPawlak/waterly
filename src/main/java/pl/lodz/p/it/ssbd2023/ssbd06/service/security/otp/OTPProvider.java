package pl.lodz.p.it.ssbd2023.ssbd06.service.security.otp;

import jakarta.ejb.Stateless;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Stateless
@Monitored
public class OTPProvider {

    public String generateOTP(final String secret) {
        return "";
    }

    public boolean verifyOTP(final String code) {
        return true;
    }

}
