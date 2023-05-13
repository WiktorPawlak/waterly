package pl.lodz.p.it.ssbd2023.ssbd06.arquillian.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.REGISTRATION;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.arquillian.config.BaseArquillianTest;
import pl.lodz.p.it.ssbd2023.ssbd06.arquillian.role.AdministratorRole;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.CreateAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.EditAccountRolesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.VerificationTokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.VerificationTokenService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

class AccountServiceTest extends BaseArquillianTest {

    @Inject
    private AccountService accountService;

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private VerificationTokenFacade verificationTokenFacade;

    @Inject
    private VerificationTokenService verificationTokenService;

    @Inject
    private AdministratorRole administratorRole;

    private final CreateAccountDto accountDto = prepareAccountDto();
    private final AccountDetails accountDetails = prepareAccountDetails();

    private static final String TEST_IP_ADDRESS = "100.100.0.0";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @SneakyThrows
    @Test
    void shouldDeleteAccountAndVerificationTokensWhenSimulatingFlow() {
        //given
        CreateAccountDto accountDto = prepareAccountDto();

        //when
        userTransaction.begin();
        accountService.registerUser(accountDto);
        List<Account> accountsBefore = accountFacade.findAll();
        List<VerificationToken> verificationTokensBefore = verificationTokenFacade.findAll();
        userTransaction.commit();

        Account account = accountsBefore.get(0);

        userTransaction.begin();
        verificationTokenService.clearTokens(account.getId(), REGISTRATION);
        accountService.removeInactiveNotConfirmedAccount(account.getId());
        userTransaction.commit();

        //then
        userTransaction.begin();
        List<Account> accountsAfter = accountFacade.findAll();
        List<VerificationToken> verificationTokensAfter = verificationTokenFacade.findAll();
        userTransaction.commit();

        assertEquals(1, accountsBefore.size());
        assertEquals(1, verificationTokensBefore.size());

        assertEquals(0, accountsAfter.size());
        assertEquals(0, verificationTokensAfter.size());
    }

    @Test
    @SneakyThrows
    void shouldChangeAccountStatus() {
        //given
        userTransaction.begin();
        accountService.registerUser(accountDto);
        userTransaction.commit();

        //when
        userTransaction.begin();
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //then
        assertFalse(createdAccount.isActive());

        //when
        userTransaction.begin();
        administratorRole.changeAccountActiveStatus(createdAccount.getId(), true);
        userTransaction.commit();

        userTransaction.begin();
        createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //then
        assertTrue(createdAccount.isActive());
    }

    @Test
    @SneakyThrows
    void shouldUpdateInfoAboutAuth() {
        //given
        userTransaction.begin();
        accountService.registerUser(accountDto);
        userTransaction.commit();

        //when
        userTransaction.begin();
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //then
        assertEquals(0, createdAccount.getAuthInfo().getIncorrectAuthCount());
        assertNull(createdAccount.getAuthInfo().getLastIpAddress());
        assertNull(createdAccount.getAuthInfo().getLastSuccessAuth());

        //when
        userTransaction.begin();
        String now = LocalDateTime.now().format(formatter);
        LocalDateTime successfulDate = LocalDateTime.parse(now, formatter);
        administratorRole.updateSuccessfulAuthInfo(successfulDate, "test", TEST_IP_ADDRESS);
        userTransaction.commit();

        userTransaction.begin();
        createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //then
        assertEquals(0, createdAccount.getAuthInfo().getIncorrectAuthCount());
        assertEquals(TEST_IP_ADDRESS, createdAccount.getAuthInfo().getLastIpAddress());
        assertEquals(successfulDate, createdAccount.getAuthInfo().getLastSuccessAuth());

        //when
        userTransaction.begin();
        now = LocalDateTime.now().format(formatter);
        LocalDateTime failedDate = LocalDateTime.parse(now, formatter);
        administratorRole.updateFailedAuthInfo(failedDate, accountDto.getLogin());
        userTransaction.commit();

        userTransaction.begin();
        createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //then
        assertEquals(1, createdAccount.getAuthInfo().getIncorrectAuthCount());
        assertEquals(failedDate, createdAccount.getAuthInfo().getLastIncorrectAuth());
    }

    @Test
    @SneakyThrows
    void shouldUpdateAccountDetails() {
        //given
        userTransaction.begin();
        accountService.registerUser(accountDto);
        userTransaction.commit();

        userTransaction.begin();
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //when
        userTransaction.begin();
        administratorRole.updateAccountDetails(createdAccount.getId(), accountDetails, createdAccount.getLocale().toLanguageTag());
        userTransaction.commit();

        userTransaction.begin();
        Account modifiedAccount = accountService.findByLogin(createdAccount.getLogin());
        userTransaction.commit();

        //then
        assertEquals("TestNew", modifiedAccount.getAccountDetails().getFirstName());
        assertEquals("TestNew", modifiedAccount.getAccountDetails().getLastName());
        assertEquals("000000000", modifiedAccount.getAccountDetails().getPhoneNumber());
    }

    @Test
    @SneakyThrows
    void shouldUpdateOwnAccountDetails() {
        //given
        userTransaction.begin();
        accountService.registerUser(accountDto);
        userTransaction.commit();

        userTransaction.begin();
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());
        userTransaction.commit();

        //when
        userTransaction.begin();
        administratorRole.updateOwnAccountDetails(createdAccount.getLogin(), accountDetails, createdAccount.getLocale().toLanguageTag());
        userTransaction.commit();

        userTransaction.begin();
        Account modifiedAccount = accountService.findByLogin(createdAccount.getLogin());
        userTransaction.commit();

        //then
        assertEquals("TestNew", modifiedAccount.getAccountDetails().getFirstName());
        assertEquals("TestNew", modifiedAccount.getAccountDetails().getLastName());
        assertEquals("000000000", modifiedAccount.getAccountDetails().getPhoneNumber());
    }

    @Test
    @SneakyThrows
    void shouldUpdateAccountRoles() {
        //given
        userTransaction.begin();
        accountService.registerUser(accountDto);
        userTransaction.commit();

        //when
        userTransaction.begin();
        Account createdAccount = accountService.findByLogin(accountDto.getLogin());

        //then
        assertEquals(0, createdAccount.getRoles().stream().filter(Role::isActive).count());
        userTransaction.commit();

        //when
        userTransaction.begin();
        EditAccountRolesDto editAccountRolesDto =
                new EditAccountRolesDto(EditAccountRolesDto.Operation.GRANT, Set.of("ADMINISTRATOR", "OWNER", "FACILITY_MANAGER"));
        administratorRole.editAccountRoles(createdAccount.getId(), editAccountRolesDto);
        userTransaction.commit();

        userTransaction.begin();
        Account modifiedAccount = accountService.findByLogin(createdAccount.getLogin());
        userTransaction.commit();

        //then
        assertEquals(3, modifiedAccount.getRoles().stream().filter(Role::isActive).count());

        //when
        userTransaction.begin();
        editAccountRolesDto =
                new EditAccountRolesDto(EditAccountRolesDto.Operation.REVOKE, Set.of("ADMINISTRATOR"));
        administratorRole.editAccountRoles(createdAccount.getId(), editAccountRolesDto);
        userTransaction.commit();

        userTransaction.begin();
        modifiedAccount = accountService.findByLogin(createdAccount.getLogin());
        userTransaction.commit();

        //then
        assertEquals(2, modifiedAccount.getRoles().stream().filter(Role::isActive).count());
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

    private AccountDetails prepareAccountDetails() {
        return new AccountDetails(null, "TestNew", "TestNew", "000000000");
    }

}
