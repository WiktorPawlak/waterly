package pl.lodz.p.it.ssbd2023.ssbd06.arquillian;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.VerificationTokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.VerificationTokenService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
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

    @SneakyThrows
    @Test
    void shouldDeleteAccountAndVerificationTokensWhenSimulatingFlow() {
        //given
        AccountDto accountDto = prepareAccountDto();

        //when
        userTransaction.begin();
        accountService.registerUser(accountDto);
        List<Account> accountsBefore = accountFacade.findAll();
        List<VerificationToken> verificationTokensBefore = verificationTokenFacade.findAll();
        userTransaction.commit();

        Account account = accountsBefore.get(0);

        userTransaction.begin();
        verificationTokenService.clearTokens(account.getId());
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

    private static AccountDto prepareAccountDto() {
        AccountDto accountDto = new AccountDto();
        accountDto.setPhoneNumber("123123123");
        accountDto.setEmail("test@test.test");
        accountDto.setLogin("test");
        accountDto.setFirstName("Test");
        accountDto.setLastName("Test");
        accountDto.setPassword("$2a$04$j/yqCtlHxKmdxHMWxaji4OD1w591LIMNDGBqUbCpD6HTM4aj2uLiS");
        accountDto.setLanguageTag("en-US");
        return accountDto;
    }

}
