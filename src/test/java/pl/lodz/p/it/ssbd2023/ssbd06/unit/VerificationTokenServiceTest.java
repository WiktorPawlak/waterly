package pl.lodz.p.it.ssbd2023.ssbd06.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.lodz.p.it.ssbd2023.ssbd06.mok.config.VerificationTokenConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.VerificationTokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.VerificationTokenService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AuthInfo;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProviderImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VerificationTokenServiceTest {

    private static final long FRI_13_FEB_2009 = 1234567890000L;
    private static final int MILLIS_IN_MINUTE = 60000;
    private static final int EXPIRATION_TIME_IN_MINUTES = 120;
    private static final int HALF_EXPIRATION_TIME_IN_MINUTES = 60;

    @InjectMocks
    private VerificationTokenService verificationTokenService;
    @Mock
    private VerificationTokenFacade verificationTokenFacade;
    @Mock
    private VerificationTokenConfig verificationTokenConfig;
    @Mock
    private TimeProviderImpl timeProvider;

    @Captor
    ArgumentCaptor<VerificationToken> verificationTokenArgumentCaptor;

    @BeforeAll
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(timeProvider.addTimeToDate(anyDouble(), any())).thenCallRealMethod();
        when(timeProvider.subractTimeFromDate(anyDouble(), any())).thenCallRealMethod();
        when(verificationTokenConfig.getExpirationTimeInMinutes()).thenReturn(EXPIRATION_TIME_IN_MINUTES);
        when(verificationTokenConfig.getHalfExpirationTimeInMinutes()).thenReturn(EXPIRATION_TIME_IN_MINUTES * .5);
    }

    @Test
    void shouldCreatePrimaryFullTimeToken() {
        // given
        Account account = new Account("test", "123", new AccountDetails(), new AuthInfo());
        Date referenceDate = new Date(FRI_13_FEB_2009);

        when(verificationTokenFacade.create(verificationTokenArgumentCaptor.capture())).thenReturn(new VerificationToken());
        when(timeProvider.currentDate()).thenReturn((referenceDate));

        // when
        verificationTokenService.createPrimaryFullTimeToken(account);

        // then
        VerificationToken token = verificationTokenArgumentCaptor.getValue();
        Date referenceDatePlusExpirationTime = new Date(FRI_13_FEB_2009 + EXPIRATION_TIME_IN_MINUTES * MILLIS_IN_MINUTE);

        assertEquals(token.getExpiryDate(), referenceDatePlusExpirationTime);
        assertEquals("test", token.getAccount().getLogin());
        assertNotNull(token.getToken());
    }

    @Test
    void shouldNotCreateSecondaryHalfTimeToken() {
        // given
        when(verificationTokenFacade.findByAccountIdAndTokenType(anyLong(), any())).thenReturn(List.of());

        // then
        assertThrows(TokenNotFoundException.class,
                () -> verificationTokenService.findOrCreateSecondaryHalfTimeToken(new Account())
        );
    }

    @Test
    void shouldNotCreateSecondaryHalfTimeTokenWhenHalfTimeExceeded() {
        // given
        Account account = new Account("test", "123", new AccountDetails(), new AuthInfo());
        VerificationToken verificationToken = VerificationToken.builder()
                .expiryDate(new Date(FRI_13_FEB_2009))
                .build();

        when(timeProvider.currentDate()).thenReturn(new Date(FRI_13_FEB_2009 + MILLIS_IN_MINUTE));
        when(verificationTokenFacade.findByAccountIdAndTokenType(anyLong(), any())).thenReturn(List.of(verificationToken));

        // then
        assertThrows(TokenExceededHalfTimeException.class,
                () -> verificationTokenService.findOrCreateSecondaryHalfTimeToken(account)
        );
    }

    @Test
    void shouldFindSecondaryHalfTimeTokenWhenExists() throws TokenExceededHalfTimeException, TokenNotFoundException {
        // given
        Account account = new Account("test", "123", new AccountDetails(), new AuthInfo());
        VerificationToken primaryToken = VerificationToken.builder()
                .expiryDate(new Date(FRI_13_FEB_2009))
                .build();
        VerificationToken secondaryToken = VerificationToken.builder()
                .expiryDate(new Date(FRI_13_FEB_2009 - HALF_EXPIRATION_TIME_IN_MINUTES * MILLIS_IN_MINUTE))
                .build();

        when(timeProvider.currentDate()).thenReturn(new Date(FRI_13_FEB_2009 - EXPIRATION_TIME_IN_MINUTES * MILLIS_IN_MINUTE));
        when(verificationTokenFacade.findByAccountIdAndTokenType(anyLong(), any())).thenReturn(List.of(primaryToken, secondaryToken));

        // when
        VerificationToken returnedSecondaryToken = verificationTokenService.findOrCreateSecondaryHalfTimeToken(account);

        // then
        assertEquals(secondaryToken, returnedSecondaryToken);
    }

    @Test
    void shouldCreateSecondaryHalfTimeTokenWhenDoesNotExist() throws TokenExceededHalfTimeException, TokenNotFoundException {
        // given
        Account account = new Account("test", "123", new AccountDetails(), new AuthInfo());
        VerificationToken primaryToken = VerificationToken.builder()
                .expiryDate(new Date(FRI_13_FEB_2009))
                .build();

        when(timeProvider.currentDate()).thenReturn(new Date(FRI_13_FEB_2009 - EXPIRATION_TIME_IN_MINUTES * MILLIS_IN_MINUTE));
        when(verificationTokenFacade.create(verificationTokenArgumentCaptor.capture())).thenReturn(new VerificationToken());
        when(verificationTokenFacade.findByAccountIdAndTokenType(anyLong(), any())).thenReturn(List.of(primaryToken));

        // when
        verificationTokenService.findOrCreateSecondaryHalfTimeToken(account);

        // then
        VerificationToken capturedSecondaryVerificationToken = verificationTokenArgumentCaptor.getValue();
        Date createdSecondaryTokenExpiryDate = capturedSecondaryVerificationToken.getExpiryDate();
        assertEquals(new Date(FRI_13_FEB_2009 - 60 * MILLIS_IN_MINUTE), createdSecondaryTokenExpiryDate);
    }

}