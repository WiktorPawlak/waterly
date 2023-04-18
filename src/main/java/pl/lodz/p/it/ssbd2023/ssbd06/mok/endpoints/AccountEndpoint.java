package pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.IdenticalPasswordsException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.UnmatchedPasswordsException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications.NotificationsProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

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

    @Inject
    @BCryptHash
    private PasswordHash hashProvider;

    @PermitAll
    public boolean checkAccountActive(final String login) {
        return accountService.checkAccountActive(login);
    }

    @RolesAllowed(ADMINISTRATOR)
    public void changeAccountActiveStatus(final long id, final boolean active) {
        accountService.changeAccountActiveStatus(id, active);
    }

    @RolesAllowed(ADMINISTRATOR)
    public void updateAccountDetails(final long id, final UpdateAccountDetailsDto updateAccountDetailsDto) {
        accountService.updateAccountDetails(id, updateAccountDetailsDto.toDomain());
    }

    @PermitAll
    public void registerUser(final AccountDto account) {
        accountService.registerUser(account);
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

    @RolesAllowed({"ADMINISTRATOR", "OWNER", "FACILITY_MANAGER"})
    public void changeOwnAccountPassword(final AccountPasswordDto accountPasswordDto) throws ApplicationBaseException {
        Account account = accountService.findByLogin(authenticatedAccount.getLogin());
        if (!hashProvider.verify(accountPasswordDto.getOldPassword().toCharArray(), account.getPassword())) {
            throw new UnmatchedPasswordsException();
        }
        if (Objects.equals(accountPasswordDto.getNewPassword(), accountPasswordDto.getOldPassword())) {
            throw new IdenticalPasswordsException();
        }
        var hashedNewPassword = hashProvider.generate(accountPasswordDto.getNewPassword().toCharArray());
        accountService.changePassword(account, hashedNewPassword);
    }

    public void acceptAccountDetailsUpdate(final long id) {
        accountService.acceptAccountDetailsUpdate(id);
    }

    @PermitAll
    public void resendEmailToAcceptAccountDetailsUpdate() {
        accountService.resendEmailToAcceptAccountDetailsUpdate(authenticatedAccount.getLogin());
    }

    @RolesAllowed(ADMINISTRATOR)
    public void editAccountRoles(final long id, final EditAccountRolesDto editAccountRolesDto) throws ApplicationBaseException {
        accountService.editAccountRoles(id, editAccountRolesDto);
    }
}
