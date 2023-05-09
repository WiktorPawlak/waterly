package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.ACCOUNT_DETAILS_UPDATE;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.CHANGE_PASSWORD;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.PASSWORD_RESET;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.REGISTRATION;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.config.VerificationTokenConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.VerificationTokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class VerificationTokenService {

    @Inject
    private VerificationTokenFacade verificationTokenFacade;
    @Inject
    private VerificationTokenConfig verificationTokenConfig;
    @Inject
    private TimeProvider timeProvider;

    @PermitAll
    public void clearTokens(final long accountId, final TokenType tokenType) {
        verificationTokenFacade.deleteByAccountIdAndTokenType(accountId, tokenType);
    }

    @PermitAll
    public VerificationToken findValidToken(final String token, final TokenType tokenType) throws TokenNotFoundException {
        return verificationTokenFacade.findValidByToken(token, tokenType)
                .orElseThrow(TokenNotFoundException::new);
    }

    @PermitAll
    public List<VerificationToken> findAllTokens() {
        return verificationTokenFacade.findAll();
    }

    @PermitAll
    public VerificationToken createPrimaryFullTimeToken(final Account account) {
        VerificationToken token = prepareToken(
                account,
                timeProvider.currentDate(),
                verificationTokenConfig.getExpirationTimeInMinutes(),
                REGISTRATION
        );
        return verificationTokenFacade.create(token);
    }

    @PermitAll
    public VerificationToken findOrCreateSecondaryHalfTimeToken(final Account account)
            throws TokenExceededHalfTimeException, TokenNotFoundException {
        List<VerificationToken> verificationTokens = verificationTokenFacade.findByAccountIdAndTokenType(account.getId(), TokenType.REGISTRATION);
        VerificationToken primaryVerificationToken = getPrimaryVerificationToken(verificationTokens);

        if (checkIfHalfTimeExceeded(primaryVerificationToken)) {
            throw ApplicationBaseException.tokenExceededHalfTimeException();
        }

        if (verificationTokens.size() > 1) {
            return getSecondaryVerificationToken(verificationTokens);
        }

        Date beginDate = timeProvider.subractTimeFromDate(verificationTokenConfig.getExpirationTimeInMinutes(), primaryVerificationToken.getExpiryDate());
        VerificationToken token = prepareToken(
                account,
                beginDate,
                verificationTokenConfig.getHalfExpirationTimeInMinutes(),
                REGISTRATION
        );
        return verificationTokenFacade.create(token);
    }

    private boolean checkIfHalfTimeExceeded(final VerificationToken verificationToken) {
        Date expiryDate = verificationToken.getExpiryDate();

        Date halfTime = timeProvider.subractTimeFromDate(verificationTokenConfig.getHalfExpirationTimeInMinutes(), expiryDate);
        Date currentTime = timeProvider.currentDate();
        return halfTime.before(currentTime);
    }

    private VerificationToken getPrimaryVerificationToken(final List<VerificationToken> verificationTokens) throws TokenNotFoundException {
        return verificationTokens.stream()
                .max(Comparator.comparing(VerificationToken::getExpiryDate))
                .orElseThrow(TokenNotFoundException::new);
    }

    private VerificationToken getSecondaryVerificationToken(final List<VerificationToken> verificationTokens) throws TokenNotFoundException {
        return verificationTokens.stream()
                .min(Comparator.comparing(VerificationToken::getExpiryDate))
                .orElseThrow(TokenNotFoundException::new);
    }

    private VerificationToken prepareToken(final Account account, final Date beginTime, final double expirationTimeInHours, final TokenType tokenType) {
        return VerificationToken.builder()
                .account(account)
                .expiryDate(timeProvider.addTimeToDate(expirationTimeInHours, beginTime))
                .token(generateToken())
                .tokenType(tokenType)
                .build();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    @PermitAll
    public VerificationToken createResetToken(final Account account) {
        VerificationToken token = prepareToken(
                account,
                timeProvider.currentDate(),
                verificationTokenConfig.getExpirationResetTimeInMinutes(),
                PASSWORD_RESET
        );
        return verificationTokenFacade.create(token);
    }

    @PermitAll
    public VerificationToken createChangePasswordToken(final Account account) {
        VerificationToken token = prepareToken(
                account,
                timeProvider.currentDate(),
                verificationTokenConfig.getExpirationChangePasswordTimeInMinutes(),
                CHANGE_PASSWORD
        );
        return verificationTokenFacade.create(token);
    }

    @PermitAll
    public VerificationToken createAcceptAccountDetailToken(final Account account) {
        VerificationToken token = prepareToken(
                account,
                timeProvider.currentDate(),
                verificationTokenConfig.getExpirationAccountDetailsInMinutes(),
                ACCOUNT_DETAILS_UPDATE
        );
        return verificationTokenFacade.create(token);
    }

    @PermitAll
    public Account confirmPassword(final UUID token, final TokenType type) throws TokenNotFoundException {
        VerificationToken verificationToken = verificationTokenFacade
                .findValidByToken(token.toString(), type)
                .orElseThrow(TokenNotFoundException::new);
        verificationTokenFacade.delete(verificationToken);
        return verificationToken.getAccount();
    }
}
