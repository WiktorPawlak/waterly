package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.sender;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.config.EmailConfig;

@RequestScoped
public class EmailSenderProviderImpl implements EmailSenderProvider {

    private final Logger log = Logger.getLogger(EmailSenderProviderImpl.class.getName());

    @Inject
    private EmailConfig emailConfig;

    @Inject Mailer mailer;

    @Override
    public void sendEmail(final String receiversEmail, final String subject, final String body) {
        mailer.send(Mail.withText(receiversEmail, subject, body));
        log.info(() -> "Email with subject: \"" + subject + "\" has been sent to user with email: " + receiversEmail);
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
