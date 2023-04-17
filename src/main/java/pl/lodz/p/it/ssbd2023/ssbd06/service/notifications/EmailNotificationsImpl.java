package pl.lodz.p.it.ssbd2023.ssbd06.service.notifications;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@PermitAll
public class EmailNotificationsImpl implements NotificationsProvider {

    @Override
    public void notifyAccountActiveStatusChanged(final long id) {
        //TODO implement
    }

    @Override
    public void notifySuccessfulAdminAuthentication(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        //TODO implement
    }

    @Override
    public void notifyWaitingAccountDetailsUpdate(final long id) {
        //TODO implement
    }

    @Override
    public void notifyRoleGranted(final long id, final Set<String> role) {
        //TODO
    }

    @Override
    public void notifyRoleRevoked(final long id, final Set<String> roleToRevoke) {
        //TODO
    }

}