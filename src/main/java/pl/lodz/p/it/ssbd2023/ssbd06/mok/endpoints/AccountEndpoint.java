package pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints;

import java.time.LocalDateTime;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.service.notifications.NotificationsProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;

@LocalBean
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountEndpoint {

    @Inject
    private AccountService accountService;

    @Inject
    private AuthenticatedAccount authenticatedAccount;

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
    public void updateOwnAccountDetails(final UpdateAccountDetailsDto updateAccountDetailsDto) {

        accountService.updateOwnAccountDetails(authenticatedAccount.getLogin(), updateAccountDetailsDto.toDomain());
    }

    @PermitAll
    public void saveSuccessfulAuthAttempt(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        accountService.updateSuccessfulAuthInfo(authenticationDate, login, ipAddress);

        if (authenticatedAccount.isAdmin()) {
            notificationsProvider.notifySuccessfulAdminAuthentication(authenticationDate, login, ipAddress);
        }
    }

    @PermitAll
    public void saveFailedAuthAttempt(final LocalDateTime authenticationDate, final String login) {
        accountService.updateFailedAuthInfo(authenticationDate, login);
    }

    public void acceptAccountDetailsUpdate(final long id) {
        accountService.acceptAccountDetailsUpdate(id);
    }

    @PermitAll
    public void resendEmailToAcceptAccountDetailsUpdate() {
        accountService.resendEmailToAcceptAccountDetailsUpdate(authenticatedAccount.getLogin());
    }

    @RolesAllowed("ADMINISTRATOR")
    public void addRoleToAccount(final long id, final String role) {
        accountService.addRoleToAccount(id, role);
    }
}
