package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.verifications;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

public interface TokenSender {

    void sendVerificationToken(VerificationToken token);

    void sendResetToken(VerificationToken token);

    void sendChangePasswordToken(VerificationToken token);

    void sendEmailUpdateAcceptToken(VerificationToken token);

    void send2FAToken(String token, Account account);
}
