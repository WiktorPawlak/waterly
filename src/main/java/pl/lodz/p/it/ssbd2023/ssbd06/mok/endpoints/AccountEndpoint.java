package pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService.FIRST_PAGE;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountSearchPreferencesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreatedAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.GetPagedAccountListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.ListAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordChangeByAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.ReCAPTCHA;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private AuthenticatedAccount authenticatedAccount;
    @Inject
    private AccountService accountService;
    @Inject
    @BCryptHash
    private PasswordHash hashProvider;

    @Inject
    private ReCAPTCHA recaptchaVerifier;
    @Inject
    @Property("default.list.page.size")
    private int defaultListPageSize;

    @RolesAllowed(ADMINISTRATOR)
    public void changeAccountActiveStatus(final long id, final AccountActiveStatusDto dto) {
        accountService.changeAccountActiveStatus(id, dto.isActive());
    }

    @RolesAllowed(ADMINISTRATOR)
    public void editAccountDetails(final long id, final EditAccountDetailsDto dto) {
        Account account = accountService.findById(id);
        if (account.calculateVersion() != dto.getVersion()) {
            throw ApplicationBaseException.optimisticLockException();
        }
        accountService.editAccountDetails(account, dto.toDomain(), dto.getLanguageTag());
    }

    @RolesAllowed({ADMINISTRATOR})
    public void editEmail(final long id, final EditEmailDto dto) {
        accountService.editEmail(id, dto.getEmail().toLowerCase());
    }

    @OnlyGuest
    public CreatedAccountDto registerUser(final CreateAccountDto account, final String recaptchaResponse) {
        boolean isRecaptchaValid = recaptchaVerifier.verifyRecaptcha(recaptchaResponse);

        if (isRecaptchaValid) {
            Long accountId = accountService.registerUser(account);
            return new CreatedAccountDto(accountId);
        } else {
           throw ApplicationBaseException.invalidRecaptchaException();
        }
    }
    @RolesAllowed(ADMINISTRATOR)
    public void createAccount(final CreateAccountDto account) {
        accountService.createAccount(account);
    }

    @OnlyGuest
    public void resendVerificationToken(final long accountId) throws TokenExceededHalfTimeException, TokenNotFoundException {
        accountService.resendVerificationToken(accountId);
    }

    @OnlyGuest
    public void confirmRegistration(final String token) throws ApplicationBaseException {
        accountService.confirmRegistration(token);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void editOwnAccountDetails(final EditAccountDetailsDto dto) {
        Account account = accountService.findByLogin(authenticatedAccount.getLogin());
        if (account.calculateVersion() != dto.getVersion()) {
            throw ApplicationBaseException.optimisticLockException();
        }
        accountService.editOwnAccountDetails(account, dto.toDomain(), dto.getLanguageTag(), dto.isTwoFAEnabled());
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void editOwnEmail(final EditEmailDto dto) {
        accountService.editOwnEmail(authenticatedAccount.getLogin(), dto.getEmail().toLowerCase());
    }

    @OnlyGuest
    public void saveSuccessfulAuthAttempt(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        accountService.updateSuccessfulAuthInfo(authenticationDate, login, ipAddress);
    }

    @OnlyGuest
    public void saveFailedAuthAttempt(final LocalDateTime authenticationDate, final String login) {
        accountService.updateFailedAuthInfo(authenticationDate, login);
    }

    @RolesAllowed({ADMINISTRATOR, OWNER, FACILITY_MANAGER})
    public void changeOwnAccountPassword(final AccountPasswordDto dto) throws ApplicationBaseException {
        Account account = accountService.findByLogin(authenticatedAccount.getLogin());
        if (!hashProvider.verify(dto.getOldPassword().toCharArray(), account.getPassword())) {
            throw ApplicationBaseException.unmatchedPasswordsException();
        }
        if (Objects.equals(dto.getNewPassword(), dto.getOldPassword())) {
            throw ApplicationBaseException.identicalPasswordsException();
        }
        var hashedNewPassword = hashProvider.generate(dto.getNewPassword().toCharArray());
        accountService.changePassword(account, hashedNewPassword);
    }

    @PermitAll
    public void acceptEmailUpdate(final String token) {
        accountService.acceptEmailUpdate(token);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public void resendEmailToAcceptAccountDetailsUpdate() {
        accountService.resendEmailToAcceptEmailUpdate(authenticatedAccount.getLogin());
    }

    @RolesAllowed(ADMINISTRATOR)
    public void editAccountRoles(final long id, final EditAccountRolesDto dto) throws ApplicationBaseException {
        accountService.editAccountRoles(id, dto);
    }

    @OnlyGuest
    public void sendResetPasswordToken(final String email) {
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isPresent()) {
            accountService.sendEmailToken(optionalAccount.get());
        } else {
            throw ApplicationBaseException.noMatchingEmailException();
        }
    }

    @RolesAllowed({ADMINISTRATOR})
    public void sendChangePasswordToken(final String email, final PasswordChangeByAdminDto dto) {
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isPresent()) {
            accountService.sendChangePasswordToken(optionalAccount.get());
            accountService.changePasswordByAdmin(dto, optionalAccount.get());
        } else {
            throw ApplicationBaseException.noMatchingEmailException();
        }
    }

    @PermitAll
    public void resetPassword(final PasswordResetDto dto) throws TokenNotFoundException {
        accountService.resetPassword(dto);
    }

    @RolesAllowed({ADMINISTRATOR, OWNER, FACILITY_MANAGER})
    public AccountDto retrieveOwnAccountDetails() {
        Account account = accountService.findByLogin(authenticatedAccount.getLogin());
        return new AccountDto(account);
    }

    @PermitAll
    public void requestForTwoFACode(final String login) {
        Account account = accountService.findByLogin(login);
        accountService.send2FAToken(account);
    }

    @PermitAll
    public Account findByLogin(final String login) {
        return accountService.findByLogin(login);
    }

    @RolesAllowed(ADMINISTRATOR)
    public PaginatedList<ListAccountDto> getAccountsList(final String pattern, final GetPagedAccountListDto dto) {
        int pageResolved = dto.getPage() != null ? dto.getPage() : FIRST_PAGE;
        int pageSizeResolved = dto.getPageSize() != null ? dto.getPageSize() : defaultListPageSize;
        String orderByResolved = dto.getOrderBy() != null ? dto.getOrderBy() : "login";

        String preparedPattern = preparePattern(pattern);

        List<ListAccountDto> accountDtoList = accountService.getAccountsList(preparedPattern,
                        pageResolved,
                        pageSizeResolved,
                        dto.getOrder(),
                        orderByResolved).stream()
                .map(ListAccountDto::new)
                .toList();

        return new PaginatedList<>(accountDtoList,
                pageSizeResolved,
                accountDtoList.size(),
                (long) Math.ceil(accountService.getAccountListCount(preparedPattern).doubleValue() / pageSizeResolved));
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
    public AccountSearchPreferencesDto getAccountsSearchPreferences() {
        return new AccountSearchPreferencesDto(accountService.getAccountSearchPreferences());
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
    public AccountDto getUserById(final long id) {
        Account account = accountService.getAccountById(id);
        return new AccountDto(account);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<ListAccountDto> getNotConfirmedAccounts() {
        return accountService.getNotConfirmedAccounts().stream()
                .map(ListAccountDto::new)
                .toList();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void rejectOwnerAccount(final long id) {
        accountService.rejectOwnerAccount(id);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void acceptOwnerAccount(final long id) {
        accountService.acceptOwnerAccount(id);
    }

    private String preparePattern(final String pattern) {
        return pattern != null && !pattern.isBlank() ? pattern.strip() : null;
    }

}
