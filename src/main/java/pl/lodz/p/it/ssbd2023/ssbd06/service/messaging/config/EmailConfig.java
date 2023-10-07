package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

@Getter
@ApplicationScoped
public class EmailConfig {

    @ConfigProperty(name = "mail.sender.name")
    private String senderName;

    @ConfigProperty(name = "mail.host")
    private String host;

    @ConfigProperty(name = "mail.port")
    private String port;

    @ConfigProperty(name = "mail.username")
    private String username;

    @ConfigProperty(name = "mail.password")
    private String password;

    @ConfigProperty(name = "mail.account-confirmation-url")
    private String accountConfirmationUrl;

    @ConfigProperty(name = "mail.reset-password-url")
    private String passwordResetUrl;

    @ConfigProperty(name = "mail.change-password-url")
    private String passwordChangeUrl;

    @ConfigProperty(name = "mail.account-detail-accept-url")
    private String accountDetailsAcceptUrl;
}
