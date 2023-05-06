package pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints;

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
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.UpdateAccountDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.AccountAlreadyExistException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@Monitored
@LocalBean
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountEndpoint {

    @Inject
    private AuthenticatedAccount authenticatedAccount;
    @Inject
    private AccountService accountService;
    @Inject
    @BCryptHash
    private PasswordHash hashProvider;

    @RolesAllowed(ADMINISTRATOR)
    public void changeAccountActiveStatus(final long id, final AccountActiveStatusDto dto) {
        accountService.changeAccountActiveStatus(id, dto.isActive());
    }

    @RolesAllowed(ADMINISTRATOR)
    public void updateAccountDetails(final long id, final UpdateAccountDetailsDto updateAccountDetailsDto) throws AccountAlreadyExistException {
        accountService.updateAccountDetails(id, updateAccountDetailsDto.toDomain());
    }

    @OnlyGuest
    public void registerUser(final AccountDto account) {
        accountService.registerUser(account);
    }

    @OnlyGuest
    public void resendVerificationToken(final long accountId) throws TokenExceededHalfTimeException, TokenNotFoundException {
        accountService.resendVerificationToken(accountId);
    }

    @OnlyGuest
    public void confirmRegistration(final String token) throws ApplicationBaseException {
        accountService.confirmRegistration(token);
    }

    @PermitAll
    public void updateOwnAccountDetails(final UpdateAccountDetailsDto updateAccountDetailsDto) throws AccountAlreadyExistException {
        accountService.updateOwnAccountDetails(authenticatedAccount.getLogin(), updateAccountDetailsDto.toDomain());
    }

    @PermitAll
    public void saveSuccessfulAuthAttempt(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        accountService.updateSuccessfulAuthInfo(authenticationDate, login, ipAddress);
    }

    @PermitAll
    public void saveFailedAuthAttempt(final LocalDateTime authenticationDate, final String login) {
        accountService.updateFailedAuthInfo(authenticationDate, login);
    }

    @RolesAllowed({ADMINISTRATOR, OWNER, FACILITY_MANAGER})
    public void changeOwnAccountPassword(final AccountPasswordDto accountPasswordDto) throws ApplicationBaseException {
        Account account = accountService.findByLogin(authenticatedAccount.getLogin());
        if (!hashProvider.verify(accountPasswordDto.getOldPassword().toCharArray(), account.getPassword())) {
            throw ApplicationBaseException.unmatchedPasswordsException();
        }
        if (Objects.equals(accountPasswordDto.getNewPassword(), accountPasswordDto.getOldPassword())) {
            throw ApplicationBaseException.identicalPasswordsException();
        }
        var hashedNewPassword = hashProvider.generate(accountPasswordDto.getNewPassword().toCharArray());
        accountService.changePassword(account, hashedNewPassword);
    }

    @PermitAll
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

    @PermitAll
    public void sendResetPasswordToken(final String email) {
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isPresent()) {
            accountService.sendEmailToken(optionalAccount.get());
        } else {
            throw ApplicationBaseException.noMatchingEmailException();
        }
    }

    @PermitAll
    public void resetPassword(final PasswordResetDto passwordResetDto) throws TokenNotFoundException {
        accountService.resetPassword(passwordResetDto);
    }

    @RolesAllowed(ADMINISTRATOR)
    public List<AccountDto> getAccounts() {
        return accountService.getAccounts().stream()
                .map(AccountDto::new)
                .toList();
    }
}
