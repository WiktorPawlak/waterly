package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications;

import java.time.LocalDateTime;
import java.util.Set;

public interface NotificationsProvider {

    void notifyAccountActiveStatusChanged(long id);

    void notifySuccessfulAdminAuthentication(LocalDateTime authenticationDate, String login, String ipAddress);

    void notifyWaitingAccountDetailsUpdate(long id);

    void notifyRoleGranted(long id, Set<String> rolesToAdd);

    void notifyRoleRevoked(long id, Set<String> rolesToRevoke);

    void notifyAccountRejected(long id);
}
