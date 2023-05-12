package pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications;

import java.time.LocalDateTime;
import java.util.Set;

import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;

public interface NotificationsProvider {

    void notifyAccountActiveStatusChanged(Account account, boolean active);

    void notifySuccessfulAdminAuthentication(LocalDateTime authenticationDate, Account account, String ipAddress);

    void notifyRoleGranted(Account account, Set<String> rolesToAdd);

    void notifyRoleRevoked(Account account, Set<String> rolesToRevoke);

    void notifyAccountRejected(Account account);

    void notifyAccountDeleted(Account account);

    void notifyAccountVerified(Account account);
}
