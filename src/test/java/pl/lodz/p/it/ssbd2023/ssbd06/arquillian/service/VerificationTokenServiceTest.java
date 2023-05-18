package pl.lodz.p.it.ssbd2023.ssbd06.arquillian.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.PASSWORD_RESET;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.REGISTRATION;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.arquillian.config.BaseArquillianTest;
import pl.lodz.p.it.ssbd2023.ssbd06.arquillian.role.AdministratorRole;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PasswordResetDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.VerificationTokenService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

public class VerificationTokenServiceTest extends BaseArquillianTest {


    @Inject
    private AccountService accountService;

    @Inject
    private VerificationTokenService verificationTokenService;

    @Inject
    private AdministratorRole administratorRole;

    private final CreateAccountDto accountDto = prepareAccountDto();

    @Test
    @SneakyThrows
    void shouldClearTokens() {
        //given
        userTransaction.begin();
        accountService.registerUser(accountDto);
        Account registeredAccount = accountService.findByLogin(accountDto.getLogin());
        List<VerificationToken> verificationTokenListBefore = verificationTokenService.findAllTokens();
        userTransaction.commit();

        //when
        userTransaction.begin();
        verificationTokenService.clearTokens(registeredAccount.getId(), REGISTRATION);
        List<VerificationToken> verificationTokenListAfter = verificationTokenService.findAllTokens();
        userTransaction.commit();

        //then
        assertEquals(1, verificationTokenListBefore.size());
        assertEquals(0, verificationTokenListAfter.size());
    }

    @Test
    @SneakyThrows
    void shouldCreatePrimaryFullTimeToken() {
        //given
        userTransaction.begin();
        administratorRole.createUser(accountDto);
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        List<VerificationToken> verificationTokenListBefore = verificationTokenService.findAllTokens();
        userTransaction.commit();

        //when
        userTransaction.begin();
        administratorRole.createPrimaryFullTimeToken(createdAccount);
        List<VerificationToken> verificationTokenListAfter = verificationTokenService.findAllTokens();
        VerificationToken createdPrimaryFullTimeToken = administratorRole.findLatestToken(createdAccount.getId(), REGISTRATION);
        userTransaction.commit();

        //then
        assertEquals(0, verificationTokenListBefore.size());
        assertEquals(1, verificationTokenListAfter.size());
        assertEquals(REGISTRATION, createdPrimaryFullTimeToken.getTokenType());
    }

    @Test
    @SneakyThrows
    void shouldCreateResetToken() {
        //given
        userTransaction.begin();
        administratorRole.createUser(accountDto);
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        List<VerificationToken> verificationTokenListBefore = verificationTokenService.findAllTokens();
        userTransaction.commit();

        //when
        userTransaction.begin();
        verificationTokenService.createResetToken(createdAccount);
        List<VerificationToken> verificationTokenListAfter = verificationTokenService.findAllTokens();
        VerificationToken createdResetToken = administratorRole.findLatestToken(createdAccount.getId(), PASSWORD_RESET);
        userTransaction.commit();

        //then
        assertEquals(0, verificationTokenListBefore.size());
        assertEquals(1, verificationTokenListAfter.size());
        assertEquals(PASSWORD_RESET, createdResetToken.getTokenType());
    }

    @Test
    @SneakyThrows
    void shouldFindOrCreateSecondaryHalfTimeToken() {
        //given
        userTransaction.begin();
        administratorRole.createUser(accountDto);
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        userTransaction.begin();
        administratorRole.createPrimaryFullTimeToken(createdAccount);
        administratorRole.createPrimaryFullTimeToken(createdAccount);
        List<VerificationToken> verificationTokenListAfterTokenCreation = verificationTokenService.findAllTokens();
        VerificationToken createdPrimaryFullTimeToken = verificationTokenListAfterTokenCreation.get(0);
        userTransaction.commit();

        //when
        userTransaction.begin();
        VerificationToken foundHalfTimeToken = administratorRole.findOrCreateSecondaryHalfTimeToken(createdAccount);
        userTransaction.commit();

        //then
        assertEquals(2, verificationTokenListAfterTokenCreation.size());
        assertEquals(createdPrimaryFullTimeToken.getId(), foundHalfTimeToken.getId());

        //when
        userTransaction.begin();
        verificationTokenService.clearTokens(createdAccount.getId(), REGISTRATION);
        List<VerificationToken> verificationTokenListAfterClearingToken = verificationTokenService.findAllTokens();
        userTransaction.commit();

        //then
        assertEquals(0, verificationTokenListAfterClearingToken.size());

        userTransaction.begin();
        administratorRole.createPrimaryFullTimeToken(createdAccount);
        VerificationToken createdHalfTimeToken = administratorRole.findOrCreateSecondaryHalfTimeToken(createdAccount);
        List<VerificationToken> verificationTokenListAfterHalfTimeTokenCreation = verificationTokenService.findAllTokens();
        userTransaction.commit();

        //then
        assertEquals(2, verificationTokenListAfterHalfTimeTokenCreation.size());
        assertEquals(REGISTRATION, createdHalfTimeToken.getTokenType());
    }

    @Test
    @SneakyThrows
    void shouldConfirmResetPassword() {
        //given
        userTransaction.begin();
        administratorRole.createUser(accountDto);
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //when
        userTransaction.begin();
        accountService.sendEmailToken(createdAccount);
        List<VerificationToken> verificationTokenListBefore = verificationTokenService.findAllTokens();
        VerificationToken token = administratorRole.findLatestToken(createdAccount.getId(), PASSWORD_RESET);
        userTransaction.commit();

        userTransaction.begin();
        administratorRole.confirmPassword(UUID.fromString(preparePasswordResetDto(verificationTokenService
                .findAllTokens().get(0).getToken()).getToken()));
        List<VerificationToken> verificationTokenListAfter = verificationTokenService.findAllTokens();
        userTransaction.commit();

        //then
        assertEquals(1, verificationTokenListBefore.size());
        assertEquals(PASSWORD_RESET, token.getTokenType());
        assertEquals(0, verificationTokenListAfter.size());
    }


    private CreateAccountDto prepareAccountDto() {
        CreateAccountDto accountDto = new CreateAccountDto();
        accountDto.setPhoneNumber("123123123");
        accountDto.setEmail("test@test.test");
        accountDto.setLogin("test");
        accountDto.setFirstName("Test");
        accountDto.setLastName("Test");
        accountDto.setPassword("password");
        accountDto.setLanguageTag("en-US");
        return accountDto;
    }

    private PasswordResetDto preparePasswordResetDto(final String token) {
        PasswordResetDto passwordResetDto = new PasswordResetDto();
        passwordResetDto.setNewPassword("newPassword");
        passwordResetDto.setToken(token);
        return passwordResetDto;
    }
}
