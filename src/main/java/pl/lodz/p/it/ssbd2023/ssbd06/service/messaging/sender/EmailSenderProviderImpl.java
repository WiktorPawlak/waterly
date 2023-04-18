package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

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
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications.EmailNotificationsImpl;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender.exceptions.EmailSenderException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EmailSenderProviderImpl implements EmailSenderProvider {

    private final Logger log = Logger.getLogger(EmailNotificationsImpl.class.getName());

    @Inject
    private EmailConfig emailConfig;

    @Override
    public void sendEmail(final String to, final String subject, final String body) {
        try {
            Message message = prepareMail(to, subject, body);
            Transport.send(message);
            log.info("Email sent successfully");
        } catch (final UnsupportedEncodingException | MessagingException e) {
            String errorMsg = "Error occurred during email sending";
            log.info(errorMsg);
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

        return Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                    }
                });
    }

}
