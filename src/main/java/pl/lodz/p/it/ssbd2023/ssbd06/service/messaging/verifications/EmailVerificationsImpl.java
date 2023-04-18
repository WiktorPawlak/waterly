package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.verifications;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender.EmailSenderProvider;

@Stateless
public class EmailVerificationsImpl implements VerificationsProvider {

    @Inject
    private EmailSenderProvider emailSenderProvider;

    // TODO: Going to be generalized in SSBD202306-66
    @Override
    public void sendVerificationToken(final VerificationToken token) {
        String email = token.getAccount().getAccountDetails().getEmail();
        emailSenderProvider.sendEmail(email, "TOPIC", "MESSAGE\n" + "url?token=" + token.getToken());
    }
}
