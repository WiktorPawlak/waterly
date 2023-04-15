package pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints;

import java.time.LocalDateTime;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.service.notifications.NotificationsProvider;

@LocalBean
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountEndpoint {

    @Inject
    private AccountService accountService;

    @Inject
    private NotificationsProvider notificationsProvider;

    @PermitAll
    public boolean checkAccountActive(final String login) {
        return accountService.checkAccountActive(login);
    }

    @RolesAllowed("ADMINISTRATOR")
    public void changeAccountActiveStatus(final long id, final boolean active) {
        accountService.changeAccountActiveStatus(id, active);
    }

    @PermitAll
    public void saveSuccessfulAuthAttempt(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        accountService.updateSuccessfulAuthInfo(authenticationDate, login, ipAddress);
        //todo send this email only to admin - waiting for Matino's implementation of AuthenticatedAccount with securityContext
        notificationsProvider.notifySuccessfulAdminAuthentication(authenticationDate, login, ipAddress);
    }

    @PermitAll
    public void saveFailedAuthAttempt(final LocalDateTime authenticationDate, final String login) {
        accountService.updateFailedAuthInfo(authenticationDate, login);
    }
}
