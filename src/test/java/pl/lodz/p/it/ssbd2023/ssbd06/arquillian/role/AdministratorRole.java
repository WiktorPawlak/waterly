package pl.lodz.p.it.ssbd2023.ssbd06.arquillian.role;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.annotation.security.RunAs;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordChangeByAdminDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.VerificationTokenService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

@Stateless
@RunAs(ADMINISTRATOR)
public class AdministratorRole {
    @Inject
    private AccountService accountService;

    @Inject
    VerificationTokenService verificationTokenService;

    public void changeAccountActiveStatus(long id, boolean active) {
        accountService.changeAccountActiveStatus(id, active);
    }

    public void updateSuccessfulAuthInfo(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        accountService.updateSuccessfulAuthInfo(authenticationDate, login, ipAddress);
    }

    public void updateFailedAuthInfo(final LocalDateTime authenticationDate, final String login) {
        accountService.updateFailedAuthInfo(authenticationDate, login);
    }

    public void updateAccountDetails(final Account account, final AccountDetails accountDetails, String languageTag) {
        accountService.editAccountDetails(account, accountDetails, languageTag);
    }

    public String getVerificationToken() {
        return verificationTokenService.findAllTokens().get(0).getToken();
    }

    public void confirmAccountTransactionWithToken(String token) {
        accountService.acceptEmailUpdate(token);
    }

    public void createUser(CreateAccountDto account) {
        accountService.createAccount(account);
    }

    public void sendChangePasswordToken(Account account) {
        accountService.sendChangePasswordToken(account);
    }

    public void changePasswordByAdmin(PasswordChangeByAdminDto password, Account account) {
        accountService.changePasswordByAdmin(password, account);
    }

    public void updateOwnAccountDetails(Account account, AccountDetails accountDetails, String languageTag) {
        accountService.editOwnAccountDetails(account, accountDetails, languageTag);
    }

    public void editAccountRoles(long id, EditAccountRolesDto editAccountRolesDto) {
        accountService.editAccountRoles(id, editAccountRolesDto);
    }

    public VerificationToken findLatestToken(final long accountId, final TokenType tokenType) {
        return verificationTokenService.findLatestToken(accountId, tokenType);
    }

    public void resetPassword(final PasswordResetDto dto) {
        accountService.resetPassword(dto);
    }

    public Account confirmPassword(final UUID token) {
        return verificationTokenService.confirmPassword(token);
    }

    public VerificationToken createPrimaryFullTimeToken(final Account account) {
        return verificationTokenService.createPrimaryFullTimeToken(account);
    }

    public VerificationToken findOrCreateSecondaryHalfTimeToken(final Account account) {
        return verificationTokenService.findOrCreateSecondaryHalfTimeToken(account);
    }

    public void confirmRegistration(final String token) {
        accountService.confirmRegistration(token);
    }
}
