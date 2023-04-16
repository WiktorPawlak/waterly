package pl.lodz.p.it.ssbd2023.ssbd06.service.notifications.email;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Getter
@ApplicationScoped
public class EmailConfig {

    @Inject
    @Property("mail.sender.name")
    private String senderName;

    @Inject
    @Property("mail.host")
    private String host;

    @Inject
    @Property("mail.port")
    private String port;

    @Inject
    @Property("mail.username")
    private String username;

    @Inject
    @Property("mail.password")
    private String password;

}
