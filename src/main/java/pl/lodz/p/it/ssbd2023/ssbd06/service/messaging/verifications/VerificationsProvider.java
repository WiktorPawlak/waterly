package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.verifications;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

public interface VerificationsProvider {

    void sendVerificationToken(VerificationToken token);

    void sendResetToken(VerificationToken token);
}
