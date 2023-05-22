package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.CONFIRMED;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.NOT_CONFIRMED;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.TO_CONFIRM;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.EMAIL_UPDATE;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.REGISTRATION;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import io.vavr.Tuple2;
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
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.RoleNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExpiredException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.ListSearchPreferencesFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.RoleFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.TwoFactorAuthenticationFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers.AccountActivationTimer;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers.AccountVerificationTimer;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AuthInfo;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.ListSearchPreferences;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Owner;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TwoFactorAuthentication;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.notifications.NotificationsProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.messaging.verifications.TokenSender;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.otp.OTPProvider;
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
    private OTPProvider otpProvider;
    @Inject
    private TwoFactorAuthenticationFacade twoFactorAuthenticationFacade;

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
    public Account findById(final Long id) {
        return accountFacade.findById(id);
    }
    
    @PermitAll
    public void changeAccountActiveStatus(final long id, final boolean active) {
        Account account = accountFacade.findById(id);
        account.setActive(active);
        account.getAuthInfo().setIncorrectAuthCount(0);
        accountFacade.update(account);
        notificationsProvider.notifyAccountActiveStatusChanged(account, active);
    }

    @OnlyGuest
    public void updateSuccessfulAuthInfo(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        var account = findByLogin(login);
        var accountAuthInfo = account.getAuthInfo();

        accountAuthInfo.setIncorrectAuthCount(0);
        accountAuthInfo.setLastSuccessAuth(authenticationDate);
        accountAuthInfo.setLastIpAddress(ipAddress);

        accountFacade.update(account);

        if (account.inRole(ADMINISTRATOR)) {
            notificationsProvider.notifySuccessfulAdminAuthentication(authenticationDate, account, ipAddress);
        }
    }

    @OnlyGuest
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
    public void editAccountDetails(final Account account, final AccountDetails accountDetails, final String languageTag) {
        updateAccountDetails(accountDetails, account, languageTag, account.isTwoFAEnabled());
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void editEmail(final long id, final String email) {
        Account account = accountFacade.findById(id);
        addAccountEmailToUpdate(account, email);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void changePassword(final Account account, final String hashedPassword) {
        account.setPassword(hashedPassword);
        accountFacade.update(account);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void editOwnAccountDetails(final Account account, final AccountDetails accountDetails, final String languageTag, final boolean twoFAEnabled) {
        updateAccountDetails(accountDetails, account, languageTag, twoFAEnabled);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void editOwnEmail(final String login, final String email) {
        Account account = findByLogin(login);
        addAccountEmailToUpdate(account, email);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void resendEmailToAcceptEmailUpdate(final String login) {
        Account account = findByLogin(login);

        VerificationToken token = verificationTokenService.findLatestToken(account.getId(), EMAIL_UPDATE);
        tokenSender.sendEmailUpdateAcceptToken(token);
    }

    @PermitAll
    public void acceptEmailUpdate(final String token) {
        VerificationToken verificationToken = verificationTokenService.findValidToken(token, EMAIL_UPDATE);
        Account account = verificationToken.getAccount();

        account.getAccountDetails().setEmail(account.getWaitingEmail());
        account.setWaitingEmail(null);

        accountFacade.update(account);
        verificationTokenService.clearTokens(account.getId(), EMAIL_UPDATE);
    }

    @RolesAllowed({ADMINISTRATOR})
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
            notificationsProvider.notifyAccountDeleted(account);
        }
    }

    @OnlyGuest
    @SneakyThrows(TokenExpiredException.class)
    public Long registerUser(final CreateAccountDto accountDto) {
        Account accountEntity = prepareAccountEntity(accountDto);
        Account persistedAccountEntity = accountFacade.create(accountEntity);

        VerificationToken token = verificationTokenService.createPrimaryFullTimeToken(persistedAccountEntity);
        accountVerificationTimer.scheduleAccountDeletion(token);

        tokenSender.sendVerificationToken(token);
        return persistedAccountEntity.getId();
    }

    @RolesAllowed({ADMINISTRATOR})
    public void createAccount(final CreateAccountDto account) {
        checkWaitingAccountEmailNotExist(account.getEmail());
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
        notificationsProvider.notifyAccountVerified(account);
        verificationTokenService.clearTokens(account.getId(), REGISTRATION);
    }

    @OnlyGuest
    public Optional<Account> findByEmail(final String email) {
        return accountFacade.findByEmail(email);
    }

    @OnlyGuest
    public void sendEmailToken(final Account account) {
        VerificationToken token = verificationTokenService.createResetToken(account);
        if (!account.isActive()) {
            throw ApplicationBaseException.notActiveAccountException();
        }
        tokenSender.sendResetToken(token);
    }

    @PermitAll
    public void send2FAToken(final Account account) {
        String twoFAToken = generateOTPPassword(account);
        if (!account.isActive()) {
            throw ApplicationBaseException.notActiveAccountException();
        }
        tokenSender.send2FAToken(twoFAToken, account);
    }

    @RolesAllowed({ADMINISTRATOR})
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
        Account account = verificationTokenService.confirmPassword(UUID.fromString(passwordResetDto.getToken()));
        if (!account.isActive()) {
            throw ApplicationBaseException.notActiveAccountException();
        }
        var hashedNewPassword = hashProvider.generate(passwordResetDto.getNewPassword().toCharArray());
        changePassword(account, hashedNewPassword);
    }

    private void checkAccountStatus(final Account account) {
        if (!account.isActive()) {
            throw ApplicationBaseException.notActiveAccountException();
        }
        if (account.getAccountState() != AccountState.CONFIRMED) {
            throw ApplicationBaseException.notConfirmedAccountException();
        }
    }

    @RolesAllowed(ADMINISTRATOR)
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

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
    public ListSearchPreferences getAccountSearchPreferences() {
        Account account = findByLogin(authenticatedAccount.getLogin());
        return listSearchPreferencesFacade.findByAccount(account).orElseThrow(AccountSearchPreferencesNotExistException::new);
    }

    @RolesAllowed({ADMINISTRATOR})
    public Long getAccountListCount(final String pattern) {
        return accountFacade.count(pattern);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public Account getAccountById(final long id) {
        return accountFacade.findById(id);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Account> getNotConfirmedAccounts() {
        return accountFacade.findNotConfirmedAccounts();
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
        accountEntity.setTwoFAEnabled(account.isTwoFAEnabled());
        accountEntity.setOtpSecret(UUID.randomUUID().toString());
        accountEntity.setLocale(Locale.forLanguageTag(account.getLanguageTag()));

        return accountEntity;
    }

    private void revokePermissions(final EditAccountRolesDto editAccountRolesDto, final Account account) throws ApplicationBaseException {
        for (String roleToRevoke : editAccountRolesDto.getRoles()) {
            performRevokePermissionOperation(account, roleToRevoke);
        }

        accountFacade.update(account);
        notificationsProvider.notifyRoleRevoked(account, editAccountRolesDto.getRoles());
    }

    private void grantPermissions(final EditAccountRolesDto editAccountRolesDto, final Account account) throws ApplicationBaseException {
        for (String roleToGrant : editAccountRolesDto.getRoles()) {
            performGrantPermissionOperation(account, roleToGrant);
        }

        accountFacade.update(account);
        notificationsProvider.notifyRoleGranted(account, editAccountRolesDto.getRoles());
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


    private void addAccountEmailToUpdate(final Account account, final String email) {
        String currentEmail = account.getAccountDetails().getEmail();
        if (!currentEmail.equalsIgnoreCase(email)) {
            checkWaitingAccountEmailNotExist(email);

            account.setWaitingEmail(email);
            accountFacade.update(account);
            tokenSender.sendEmailUpdateAcceptToken(verificationTokenService.createAcceptEmailToken(account));
            log.info("Added account email waiting for accept: " + account.getId());
        }
    }

    private void checkWaitingAccountEmailNotExist(final String email) {
        accountFacade.findByWaitingEmail(email).ifPresent(it -> {
            throw ApplicationBaseException.accountWithEmailAlreadyExist();
        });
    }

    private void updateAccountDetails(final AccountDetails newAccountDetails, final Account account, final String languageTag, final boolean twoFAEnabled) {
        account.setLocale(Locale.forLanguageTag(languageTag));
        account.setTwoFAEnabled(twoFAEnabled);
        AccountDetails accountDetails = account.getAccountDetails();

        accountDetails.setFirstName(newAccountDetails.getFirstName());
        accountDetails.setLastName(newAccountDetails.getLastName());
        accountDetails.setPhoneNumber(newAccountDetails.getPhoneNumber());
        accountFacade.update(account);
        log.info("Account details updated: " + account.getId());
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void rejectOwnerAccount(final long id) {
        Account account = accountFacade.findById(id);
        if (Objects.equals(account, null)) {
            throw ApplicationBaseException.accountDoesNotExistException();
        }
        if (Objects.equals(account.getAccountState(), TO_CONFIRM)) {
            accountFacade.delete(account);
            notificationsProvider.notifyAccountRejected(account);
        } else {
            throw ApplicationBaseException.accountNotWaitingForConfirmation();
        }
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void acceptOwnerAccount(final long id) {
        Account account = accountFacade.findById(id);
        if (Objects.equals(account, null)) {
            throw ApplicationBaseException.accountDoesNotExistException();
        }
        if (Objects.equals(account.getAccountState(), TO_CONFIRM)) {
            account.setAccountState(CONFIRMED);
            Role ownerRole = account.getRoles().stream()
                    .filter(Owner.class::isInstance)
                    .findFirst()
                    .orElseThrow(RoleNotFoundException::new);
            ownerRole.setActive(true);
            accountFacade.update(account);
            notificationsProvider.notifyAccountAccepted(account);
        } else {
            throw ApplicationBaseException.accountNotWaitingForConfirmation();
        }
    }

    private boolean isModifyingAnotherUser(final Account account) {
        return !account.getLogin().equals(authenticatedAccount.getLogin());
    }

    public String generateOTPPassword(final Account account) {
        Tuple2<TwoFactorAuthentication, String> otp = otpProvider.generateOTPPassword(account);
        twoFactorAuthenticationFacade.create(otp._1);
        return otp._2;
    }

    public boolean verifyOTP(final Account account, final String code) {
        TwoFactorAuthentication twoFA = twoFactorAuthenticationFacade.findByAccount(account);
        return otpProvider.verifyOTP(code, twoFA);
    }

}
