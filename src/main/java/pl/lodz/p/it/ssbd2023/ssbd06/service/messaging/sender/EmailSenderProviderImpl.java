package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.config.EmailConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender.exceptions.EmailSenderException;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class EmailSenderProviderImpl implements EmailSenderProvider {

    private final Logger log = Logger.getLogger(EmailSenderProviderImpl.class.getName());

    @Inject
    private EmailConfig emailConfig;

    @Override
    @Asynchronous
    public void sendEmail(final String receiversEmail, final String subject, final String body) {
        try {
            Message message = prepareMail(receiversEmail, subject, body);
            Transport.send(message);
            log.info(() -> "Email with subject: \"" + subject + "\" has been sent to user with email: " + receiversEmail);
        } catch (final UnsupportedEncodingException | MessagingException e) {
            String errorMsg = "Exception while sending email with subject: \"" + subject
                    + "\" to user with email: " + receiversEmail + ". Cause: " + e.getMessage();
            log.severe(() -> errorMsg);
            throw new EmailSenderException(errorMsg, e);
        }
    }

    private Message prepareMail(final String to, final String subject, final String body) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(prepareSession());
        message.setFrom(new InternetAddress(emailConfig.getUsername(), emailConfig.getSenderName()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    private Session prepareSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailConfig.getHost());
        props.put("mail.smtp.port", emailConfig.getPort());

        return Session.getInstance(props, prepareAuthenticator());
    }

    private Authenticator prepareAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
            }
        };
    }

}
