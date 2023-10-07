package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender;

import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;

@RequestScoped
@Alternative
public class EmailSenderProviderMock implements EmailSenderProvider {

    private final Logger log = Logger.getLogger(EmailSenderProviderMock.class.getName());

    @Override
    public void sendEmail(final String to, final String subject, final String body) {
        log.info("Mocked sendEmail() method invoked");
    }

}
