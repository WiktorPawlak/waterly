package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.CONFIRMED;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.NOT_CONFIRMED;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.TO_CONFIRM;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.ACCOUNT_DETAILS_UPDATE;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.REGISTRATION;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordChangeByAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.AccountSearchPreferencesNotExistException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExpiredException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.ListSearchPreferencesFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.RoleFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers.AccountActivationTimer;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers.AccountVerificationTimer;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AuthInfo;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.ListSearchPreferences;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications.NotificationsProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.verifications.TokenSender;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountService {

    public static final int FIRST_PAGE = 1;
    private final Logger log = Logger.getLogger(AccountService.class.getName());

    @Inject
    private AccountFacade accountFacade;
    @Inject
    private RoleFacade roleFacade;
    @Inject
    private ListSearchPreferencesFacade listSearchPreferencesFacade;
    @Inject
    private NotificationsProvider notificationsProvider;
    @Inject
    private TokenSender tokenSender;
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
    public Account findByLogin(final String login) {
        Optional<Account> optionalAccount = accountFacade.findByLogin(login);

        if (optionalAccount.isEmpty()) {
            log.info(() -> "Account with login:" + login + " does not exist");
            throw ApplicationBaseException.accountDoesNotExistException();
        }

        return optionalAccount.get();
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
        var account = findByLogin(login);
        var accountAuthInfo = account.getAuthInfo();

        accountAuthInfo.setIncorrectAuthCount(0);
        accountAuthInfo.setLastSuccessAuth(authenticationDate);
        accountAuthInfo.setLastIpAddress(ipAddress);

        accountFacade.update(account);

        if (authenticatedAccount.isAdmin()) {
            notificationsProvider.notifySuccessfulAdminAuthentication(authenticationDate, login, ipAddress);
        }
    }

    @PermitAll
    public void updateFailedAuthInfo(final LocalDateTime authenticationDate, final String login) {
        var account = findByLogin(login);
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

    @RolesAllowed({ADMINISTRATOR})
    public void updateAccountDetails(final long id, final AccountDetails accountDetails) {
        Account account = accountFacade.findById(id);
        addAccountDetailsToUpdate(account, accountDetails);
    }

    @PermitAll
    public void changePassword(final Account account, final String hashedPassword) {
        account.setPassword(hashedPassword);
        accountFacade.update(account);
    }

    @PermitAll
    public void updateOwnAccountDetails(final String login, final AccountDetails accountDetails) {
        Account account = findByLogin(login);
        addAccountDetailsToUpdate(account, accountDetails);
    }

    @PermitAll
    public void resendEmailToAcceptAccountDetailsUpdate(final String login) {
        Account account = findByLogin(login);

        //TODO resend email token
        notificationsProvider.notifyWaitingAccountDetailsUpdate(account.getId());
    }

    @PermitAll
    public void acceptAccountDetailsUpdate(final String token) {
        VerificationToken verificationToken = verificationTokenService.findValidToken(token, ACCOUNT_DETAILS_UPDATE);
        Account account = verificationToken.getAccount();

        account.setAccountDetails(account.getWaitingAccountDetails());
        account.setWaitingAccountDetails(null);

        accountFacade.update(account);
        //TODO czy to usuwa wszystkie tokeny?
        verificationTokenService.clearTokens(account.getId(), ACCOUNT_DETAILS_UPDATE);
    }

    @PermitAll
    public void editAccountRoles(final long id, final EditAccountRolesDto editAccountRolesDto) throws ApplicationBaseException {
        var account = accountFacade.findById(id);
        if (isModifyingAnotherUser(account)) {
            switch (editAccountRolesDto.getOperation()) {
                case GRANT -> grantPermissions(editAccountRolesDto, account);
                case REVOKE -> revokePermissions(editAccountRolesDto, account);
                default -> throw ApplicationBaseException.operationUnsupportedException();
            }
        } else {
            throw ApplicationBaseException.forbiddenOperationException();
        }
    }

    @PermitAll
    public void removeInactiveNotConfirmedAccount(final long id) {
        Account account = accountFacade.findById(id);
        if (account != null && !account.isActive() && Objects.equals(account.getAccountState(), NOT_CONFIRMED)) {
            accountFacade.delete(account);
        }
    }

    @OnlyGuest
    @SneakyThrows(TokenExpiredException.class)
    public void registerUser(final CreateAccountDto accountDto) {
        Account accountEntity = prepareAccountEntity(accountDto);
        Account persistedAccountEntity = accountFacade.create(accountEntity);

        VerificationToken token = verificationTokenService.createPrimaryFullTimeToken(persistedAccountEntity);
        accountVerificationTimer.scheduleAccountDeletion(token);

        tokenSender.sendVerificationToken(token);
    }

    @RolesAllowed({ADMINISTRATOR})
    public void createUser(final CreateAccountDto account) {
        Account accountEntity = prepareAccountEntity(account);
        accountEntity.setAccountState(CONFIRMED);
        accountEntity.setActive(true);
        accountFacade.create(accountEntity);
    }

    @OnlyGuest
    public void resendVerificationToken(final long accountId) throws TokenNotFoundException, TokenExceededHalfTimeException {
        Account account = accountFacade.findById(accountId);

        VerificationToken halfTimeToken = verificationTokenService.findOrCreateSecondaryHalfTimeToken(account);
        tokenSender.sendVerificationToken(halfTimeToken);
    }

    @OnlyGuest
    public void confirmRegistration(final String token) throws ApplicationBaseException {
        VerificationToken verificationToken = verificationTokenService.findValidToken(token, REGISTRATION);
        Account account = verificationToken.getAccount();
        accountVerificationTimer.cancelAccountDeletion(account.getId());

        account.setActive(true);
        account.getAuthInfo().setIncorrectAuthCount(0);
        account.setAccountState(TO_CONFIRM);
        accountFacade.update(account);
        verificationTokenService.clearTokens(account.getId(), REGISTRATION);
    }

    @PermitAll
    public Optional<Account> findByEmail(final String email) {
        return accountFacade.findByEmail(email);
    }

    @PermitAll
    public void sendEmailToken(final Account account) {
        VerificationToken token = verificationTokenService.createResetToken(account);
        checkAccountStatus(account);
        tokenSender.sendResetToken(token);
    }

    @PermitAll
    public void sendChangePasswordToken(final Account account) {
        VerificationToken token = verificationTokenService.createChangePasswordToken(account);
        checkAccountStatus(account);
        tokenSender.sendChangePasswordToken(token);
    }

    @RolesAllowed(ADMINISTRATOR)
    public void changePasswordByAdmin(final PasswordChangeByAdminDto changeByAdminDto, final Account account) {
        checkAccountStatus(account);
        var hashedNewPassword = hashProvider.generate(changeByAdminDto.getNewPassword().toCharArray());
        changePassword(account, hashedNewPassword);
    }

    @PermitAll
    public void resetPassword(final PasswordResetDto passwordResetDto) throws TokenNotFoundException {
        TokenType type = passwordResetDto.getType();
        Account account = verificationTokenService.confirmPassword(UUID.fromString(passwordResetDto.getToken()), type);
        checkAccountStatus(account);
        var hashedNewPassword = hashProvider.generate(passwordResetDto.getNewPassword().toCharArray());
        changePassword(account, hashedNewPassword);
    }

    @PermitAll
    public void checkAccountStatus(final Account account) {
        if (!account.isActive()) {
            throw ApplicationBaseException.notActiveAccountException();
        }
        if (account.getAccountState() != AccountState.CONFIRMED) {
            throw ApplicationBaseException.notConfirmedAccountException();
        }
    }

    @PermitAll
    public List<Account> getAccounts() {
        return accountFacade.findAll();
    }

    @PermitAll
    public List<Account> getAccountsList(final String pattern,
                                         final Integer page,
                                         final Integer pageSize,
                                         final String order,
                                         final String orderBy) {
        boolean ascOrder = "asc".equalsIgnoreCase(order);

        Account account = findByLogin(authenticatedAccount.getLogin());

        Optional<ListSearchPreferences> accountSearchPreferences = listSearchPreferencesFacade.findByAccount(account);
        updateOrCreateAccountSearchPreferences(order, orderBy, pageSize, account, accountSearchPreferences);

        return accountFacade.findAccounts(pattern,
                page,
                pageSize,
                ascOrder,
                orderBy);
    }

    @PermitAll
    public ListSearchPreferences getAccountSearchPreferences() {
        Account account = findByLogin(authenticatedAccount.getLogin());
        return listSearchPreferencesFacade.findByAccount(account).orElseThrow(AccountSearchPreferencesNotExistException::new);
    }

    @PermitAll
    public Long getAccountListCount(final String pattern) {
        return accountFacade.count(pattern);
    }

    @PermitAll
    public Account geAccountById(final long id) {
        return accountFacade.findById(id);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Account> getNotAcceptedAccounts() {
        return accountFacade.findNotAcceptedAccounts();
    }

    private void updateOrCreateAccountSearchPreferences(final String order,
                                                        final String orderBy,
                                                        final int pageSize,
                                                        final Account account,
                                                        final Optional<ListSearchPreferences> accountSearchPreferences) {
        accountSearchPreferences.ifPresentOrElse(searchPreferences -> {
            searchPreferences.setPageSize(pageSize);
            searchPreferences.setSortingOrder(order);
            searchPreferences.setOrderBy(orderBy);
            listSearchPreferencesFacade.update(searchPreferences);
        }, () -> {
            ListSearchPreferences newPreferences = new ListSearchPreferences(account, pageSize, orderBy, order);
            listSearchPreferencesFacade.create(newPreferences);
        });
    }

    private Account prepareAccountEntity(final CreateAccountDto account) {
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
        accountEntity.setLocale(Locale.forLanguageTag(account.getLanguageTag()));

        return accountEntity;
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
            throw ApplicationBaseException.cannotModifyPermissionsException();
        }
        foundRole.ifPresent(optRole -> optRole.setActive(true));
    }

    private void performRevokePermissionOperation(final Account account, final String role) throws ApplicationBaseException {
        Optional<Role> roleToRemove = roleFacade.findRoleByAccountAndPermissionLevel(account, role);
        if (roleToRemove.isPresent() && (!roleToRemove.get().isActive())) {
            log.info(() -> "Account has no granted " + role + " role");
            throw ApplicationBaseException.cannotModifyPermissionsException();
        }
        roleToRemove.ifPresent(optRole -> optRole.setActive(false));
    }

    private void addAccountDetailsToUpdate(final Account account, final AccountDetails accountDetails) {
        String currentAccountEmail = account.getAccountDetails().getEmail();
        if (currentAccountEmail.equalsIgnoreCase(accountDetails.getEmail())) {
            updateAccountDetails(accountDetails, account);
            accountFacade.update(account);
            log.info("Account details updated: " + account.getId());
        } else {
            account.setWaitingAccountDetails(accountDetails);
            accountFacade.update(account);
            tokenSender.accountDetailsAcceptToken(verificationTokenService.createAcceptAccountDetailToken(account));
            log.info("Added account details waiting for accept: " + account.getId());
        }
    }

    private void updateAccountDetails(final AccountDetails newAccountDetails, final Account account) {
        AccountDetails currentAccountDetails = account.getAccountDetails();

        currentAccountDetails.setFirstName(newAccountDetails.getFirstName());
        currentAccountDetails.setLastName(newAccountDetails.getLastName());
        currentAccountDetails.setPhoneNumber(newAccountDetails.getPhoneNumber());
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void rejectOwnerAccount(final long id) {
        Account account = accountFacade.findById(id);
        if (Objects.equals(account, null)) {
            throw ApplicationBaseException.accountDoesNotExistException();
        }
        if (Objects.equals(account.getAccountState(), TO_CONFIRM)) {
            accountFacade.delete(account);
            notificationsProvider.notifyAccountRejected(id);
        } else {
            throw ApplicationBaseException.accountNotWaitingForConfirmation();
        }
    }

    private boolean isModifyingAnotherUser(final Account account) {
        return !account.getLogin().equals(authenticatedAccount.getLogin());
    }
}
