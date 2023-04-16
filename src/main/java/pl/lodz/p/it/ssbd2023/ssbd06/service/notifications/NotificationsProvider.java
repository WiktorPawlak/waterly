package pl.lodz.p.it.ssbd2023.ssbd06.service.notifications;

import java.time.LocalDateTime;

public interface NotificationsProvider {

    void notifyAccountActiveStatusChanged(long id);

    void notifySuccessfulAdminAuthentication(LocalDateTime authenticationDate, String login, String ipAddress);

    void notifyWaitingAccountDetailsUpdate(long id);

    void notifyRoleGranted(long id, String role);
}
