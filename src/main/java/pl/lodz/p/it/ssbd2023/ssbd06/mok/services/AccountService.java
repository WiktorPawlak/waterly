package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.TO_CONFIRM;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.AccountAlreadyExist;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.CannotModifyPermissionsException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.ForbiddenOperationException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.OperationUnsupportedException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.RoleFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AuthInfo;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications.NotificationsProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.verifications.VerificationsProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@Monitored
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
    private VerificationsProvider verificationsProvider;
    @Inject
    private VerificationTokenService verificationTokenService;
    @Inject
    private AccountVerificationTimer accountVerificationTimer;
    @Inject
    private AccountActivationTimer accountActivationTimer;
    @Inject
    private AuthenticatedAccount authenticatedAccount;
    @Inject
    @BCryptHash
    private PasswordHash hashProvider;

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

        if (++authAttemptsCount == authAttempts && account.isActive()) {
            this.changeAccountActiveStatus(account.getId(), false);
            accountActivationTimer.scheduleAccountActivation(account.getId());
        }

        accountFacade.update(account);
    }

    @RolesAllowed(ADMINISTRATOR)
    public void updateAccountDetails(final long id, final AccountDetails accountDetails) throws AccountAlreadyExist {
        Account account = accountFacade.findById(id);
        addAccountDetailsToUpdate(account, accountDetails);
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
    public void updateOwnAccountDetails(final String login, final AccountDetails accountDetails) throws AccountAlreadyExist {
        Account account = accountFacade.findByLogin(login);
        addAccountDetailsToUpdate(account, accountDetails);
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

    @PermitAll
    public void editAccountRoles(final long id, final EditAccountRolesDto editAccountRolesDto) throws ApplicationBaseException {
        var account = accountFacade.findById(id);
        if (isModifyingAnotherUser(account)) {
            switch (editAccountRolesDto.getOperation()) {
                case GRANT -> grantPermissions(editAccountRolesDto, account);
                case REVOKE -> revokePermissions(editAccountRolesDto, account);
                default -> throw new OperationUnsupportedException("Unsupported operation");
            }
        } else {
            throw new ForbiddenOperationException("Forbidden operation");
        }
    }

    @PermitAll
    public void removeInactiveNotConfirmedAccount(final long id) {
        // TODO: fix account deletion
//        Account account = accountFacade.findById(id);
//        if (account != null && !account.isActive() && Objects.equals(account.getAccountState(), NOT_CONFIRMED)) {
//            accountFacade.delete(account);
//        }
    }

    private void revokePermissions(final EditAccountRolesDto editAccountRolesDto, final Account account) throws ApplicationBaseException {
        for (String roleToRevoke : editAccountRolesDto.getRoles()) {
            performRevokePermissionOperation(account, roleToRevoke);
        }

        accountFacade.update(account);
        notificationsProvider.notifyRoleRevoked(account.getId(), editAccountRolesDto.getRoles());
    }

    private void grantPermissions(final EditAccountRolesDto editAccountRolesDto, final Account account) throws ApplicationBaseException {
        for (String roleToGrant : editAccountRolesDto.getRoles()) {
            performGrantPermissionOperation(account, roleToGrant);
        }

        accountFacade.update(account);
        notificationsProvider.notifyRoleGranted(account.getId(), editAccountRolesDto.getRoles());
    }

    private void performGrantPermissionOperation(final Account account, final String role) throws ApplicationBaseException {
        Optional<Role> foundRole = roleFacade.findRoleByAccountAndPermissionLevel(account, role);
        if (foundRole.isPresent() && (foundRole.get().isActive())) {
            log.info(() -> "Account has already granted " + role + " role");
            throw new CannotModifyPermissionsException("Account has already granted " + role + " role");

        }
        foundRole.ifPresent(optRole -> optRole.setActive(true));
    }

    private void performRevokePermissionOperation(final Account account, final String role) throws ApplicationBaseException {
        Optional<Role> roleToRemove = roleFacade.findRoleByAccountAndPermissionLevel(account, role);
        if (roleToRemove.isPresent() && (!roleToRemove.get().isActive())) {
            log.info(() -> "Account has no granted " + role + " role");
            throw new CannotModifyPermissionsException("Account has no granted " + role + " role");

        }
        roleToRemove.ifPresent(optRole -> optRole.setActive(false));
    }

    private void addAccountDetailsToUpdate(final Account account, final AccountDetails accountDetails) throws AccountAlreadyExist {
        String currentAccountEmail = account.getAccountDetails().getEmail();

        Optional<Account> optionalAccount = accountFacade.findByEmail(accountDetails.getEmail());

        if (optionalAccount.isPresent()) {
            log.info("Account details update error: account with email" + accountDetails.getEmail() + "already exist" + account.getId());
            throw new AccountAlreadyExist("Account already exist with email: " + accountDetails.getEmail());
        }

        if (currentAccountEmail.equalsIgnoreCase(accountDetails.getEmail())) {
            updateAccountDetails(accountDetails, account);
            log.info("Account details updated: " + account.getId());
        } else {
            account.setWaitingAccountDetails(accountDetails);
            accountFacade.update(account);
            notificationsProvider.notifyWaitingAccountDetailsUpdate(account.getId());
            log.info("Added account details waiting for accept: " + account.getId());
        }
    }

    private void updateAccountDetails(final AccountDetails newAccountDetails, final Account account) {
        AccountDetails currentAccountDetails = account.getAccountDetails();

        currentAccountDetails.setFirstName(newAccountDetails.getFirstName());
        currentAccountDetails.setLastName(newAccountDetails.getLastName());
        currentAccountDetails.setPhoneNumber(newAccountDetails.getPhoneNumber());

        accountFacade.update(account);
    }

    private boolean isModifyingAnotherUser(final Account account) {
        return !account.getLogin().equals(authenticatedAccount.getLogin());
    }

    @OnlyGuest
    public void registerUser(final AccountDto accountDto) {
        Account accountEntity = prepareAccountEntity(accountDto);
        Account persistedAccountEntity = accountFacade.create(accountEntity);

        VerificationToken token = verificationTokenService.createToken(persistedAccountEntity);
        accountVerificationTimer.scheduleAccountDeletion(persistedAccountEntity.getId());

        verificationsProvider.sendVerificationToken(token);
    }

    @PermitAll
    public void confirmRegistration(final String token) throws ApplicationBaseException {
        VerificationToken verificationToken = verificationTokenService.findToken(token);
        if (verificationToken == null) {
            throw new TokenNotFoundException();
        }
        Account account = verificationToken.getAccount();
        accountVerificationTimer.cancelAccountDeletion(account.getId());

        account.setActive(true);
        account.getAuthInfo().setIncorrectAuthCount(0);
        account.setAccountState(TO_CONFIRM);
        accountFacade.update(account);
    }

    private Account prepareAccountEntity(final AccountDto account) {
        var accountDetails = new AccountDetails(account.getEmail(), account.getFirstName(),
                account.getLastName(), account.getPhoneNumber());
        var authInfo = new AuthInfo();
        var hashedPassword = hashProvider.generate(account.getPassword().toCharArray());
        var accountEntity = new Account(account.getLogin(), hashedPassword,
                accountDetails, authInfo);
        authInfo.setAccount(accountEntity);
        accountEntity.setAuthInfo(authInfo);
        Set<Role> roles = Set.of(Role.valueOf("ADMINISTRATOR"),
                Role.valueOf("FACILITY_MANAGER"), Role.valueOf("OWNER"));
        roles.forEach(role -> {
            role.setActive(false);
            role.setAccount(accountEntity);
        });
        accountEntity.setRoles(roles);

        return accountEntity;
    }

}
