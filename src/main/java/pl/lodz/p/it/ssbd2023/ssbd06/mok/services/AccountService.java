package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.OperationForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.RoleFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.notifications.NotificationsProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountService {

    private final Logger log = Logger.getLogger(AccountService.class.getName());

    @Inject
    private AccountFacade accountFacade;
    @Inject
    private RoleFacade roleFacade;
    @Inject
    private NotificationsProvider notificationsProvider;
    @Inject
    private AccountActivationTimer accountActivationTimer;
    @Inject
    private AuthenticatedAccount authenticatedAccount;

    @Inject
    @Property("auth.attempts")
    private int authAttempts;

    @PermitAll
    public boolean checkAccountActive(final String login) {
        return accountFacade.findByLogin(login).isActive();
    }

    @PermitAll
    public void changeAccountActiveStatus(final long id, final boolean active) {
        Account account = accountFacade.findById(id);
        account.setActive(active);
        account.getAuthInfo().setIncorrectAuthCount(0);
        accountFacade.update(account);
        notificationsProvider.notifyAccountActiveStatusChanged(id);
    }

    @PermitAll
    public void updateSuccessfulAuthInfo(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        var account = accountFacade.findByLogin(login);
        var accountAuthInfo = account.getAuthInfo();

        accountAuthInfo.setIncorrectAuthCount(0);
        accountAuthInfo.setLastSuccessAuth(authenticationDate);
        accountAuthInfo.setLastIpAddress(ipAddress);

        accountFacade.update(account);
    }

    @PermitAll
    public void updateFailedAuthInfo(final LocalDateTime authenticationDate, final String login) {
        var account = accountFacade.findByLogin(login);
        var accountAuthInfo = account.getAuthInfo();
        var authAttemptsCount = accountAuthInfo.getIncorrectAuthCount();

        accountAuthInfo.setIncorrectAuthCount(authAttemptsCount + 1);
        accountAuthInfo.setLastIncorrectAuth(authenticationDate);

        if (++authAttemptsCount == authAttempts) {
            this.changeAccountActiveStatus(account.getId(), false);
            accountActivationTimer.scheduleAccountActivation(account.getId());
        }

        accountFacade.update(account);
    }

    @PermitAll
    public Account findByLogin(final String login) {
        return accountFacade.findByLogin(login);
    }

    @PermitAll
    public void changePassword(final Account account, final String hashedPassword) {
        account.setPassword(hashedPassword);
        accountFacade.update(account);
    }

    @PermitAll
    public void updateOwnAccountDetails(final String login, final AccountDetails accountDetails) {
        Account account = accountFacade.findByLogin(login);
        String currentAccountEmail = account.getAccountDetails().getEmail();

        if (currentAccountEmail.equalsIgnoreCase(accountDetails.getEmail())) {
            updateAccountDetails(accountDetails, account);
        } else {
            account.setWaitingAccountDetails(accountDetails);
            accountFacade.update(account);
            notificationsProvider.notifyWaitingAccountDetailsUpdate(account.getId());
        }
    }

    @PermitAll
    public void resendEmailToAcceptAccountDetailsUpdate(final String login) {
        Account account = accountFacade.findByLogin(login);

        notificationsProvider.notifyWaitingAccountDetailsUpdate(account.getId());
    }

    public void acceptAccountDetailsUpdate(final long id) {
        Account account = accountFacade.findByWaitingAccountDetailsId(id);

        account.setAccountDetails(account.getWaitingAccountDetails());
        account.setWaitingAccountDetails(null);

        accountFacade.update(account);
    }

    private void updateAccountDetails(final AccountDetails newAccountDetails, final Account account) {
        AccountDetails currentAccountDetails = account.getAccountDetails();

        currentAccountDetails.setFirstName(newAccountDetails.getFirstName());
        currentAccountDetails.setLastName(newAccountDetails.getLastName());
        currentAccountDetails.setPhoneNumber(newAccountDetails.getPhoneNumber());

        accountFacade.update(account);
    }

    @PermitAll
    public void editAccountRoles(final long id, final EditAccountRolesDto editAccountRolesDto) {
        var account = accountFacade.findById(id);
        if (isModifyingAnotherUser(account)) {
            switch (editAccountRolesDto.getOperation()) {
                case GRANT -> editAccountRolesDto.getRoles().forEach(roleToGrant -> performGrantPermissionOperation().accept(account, roleToGrant));
                case REVOKE -> editAccountRolesDto.getRoles().forEach(roleToRevoke -> performRevokePermissionOperation().accept(account, roleToRevoke));
                default -> throw new UnsupportedOperationException("Unsupported operation");
            }
        } else {
            throw new OperationForbiddenException("Forbidden operation");
        }
    }

    @PermitAll
    private BiConsumer<Account, String> performGrantPermissionOperation() {
        return (account, role) -> {
            Set<Role> accountRoles = account.getRoles();
            Role roleToAdd = Role.valueOf(role);
            if (!checkUserHasRole(account, role)) {
                roleToAdd.setAccount(account);
                roleToAdd.setCreatedBy(account);
                accountRoles.add(roleToAdd);
                accountFacade.update(account);
                notificationsProvider.notifyRoleGranted(account.getId(), role);
            }
        };
    }

    @PermitAll
    private BiConsumer<Account, String> performRevokePermissionOperation() {
        return (account, role) -> {
            Set<Role> accountRoles = account.getRoles();
            Optional<Role> roleToRemove = roleFacade.findRoleByAccountAndPermissionLevel(account, role);
            removePermission(account, role, accountRoles, roleToRemove);
        };
    }

    private void removePermission(final Account account, final String role, final Set<Role> accountRoles, final Optional<Role> roleToRemove) {
        roleToRemove.ifPresentOrElse(optRole -> {
            accountRoles.remove(optRole);
            accountFacade.update(account);
            roleFacade.delete(optRole);
            notificationsProvider.notifyRoleRevoked(account.getId(), role);
        }, () -> log.info("Account has no granted " + role + " role"));
    }

    private boolean checkUserHasRole(final Account account, final String role) {
        return roleFacade.findRoleByAccountAndPermissionLevel(account, role).isPresent();
    }

    private boolean isModifyingAnotherUser(final Account account) {
        return !account.getLogin().equals(authenticatedAccount.getLogin());
    }

}
