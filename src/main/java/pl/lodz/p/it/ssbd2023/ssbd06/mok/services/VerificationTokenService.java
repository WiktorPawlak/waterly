package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.config.VerificationTokenConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.VerificationTokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class VerificationTokenService {

    @Inject
    private VerificationTokenFacade verificationTokenFacade;
    @Inject
    private VerificationTokenConfig verificationTokenConfig;
    @Inject
    private DateProvider dateProvider;

    @PermitAll
    public void clearTokens(final long accountId) {
        verificationTokenFacade.deleteByAccountId(accountId);
    }

    @PermitAll
    public VerificationToken findValidToken(final String token) throws TokenNotFoundException {
        return verificationTokenFacade.findValidByToken(token)
                .orElseThrow(TokenNotFoundException::new);
    }

    @PermitAll
    public List<VerificationToken> findAllTokens() {
        return verificationTokenFacade.findAll();
    }

    @PermitAll
    public VerificationToken createPrimaryFullTimeToken(final Account account) {
        VerificationToken token = prepareToken(account, dateProvider.currentDate(), verificationTokenConfig.getExpirationTimeInHours());
        return verificationTokenFacade.create(token);
    }

    @PermitAll
    public VerificationToken findOrCreateSecondaryHalfTimeToken(final Account account)
            throws TokenExceededHalfTimeException, TokenNotFoundException {
        List<VerificationToken> verificationTokens = verificationTokenFacade.findByAccountId(account.getId());
        VerificationToken primaryVerificationToken = getPrimaryVerificationToken(verificationTokens);

        if (checkIfHalfTimeExceeded(primaryVerificationToken)) {
            throw new TokenExceededHalfTimeException();
        }

        if (verificationTokens.size() > 1) {
            return getSecondaryVerificationToken(verificationTokens);
        }

        Date beginDate = dateProvider.subractTimeFromDate(verificationTokenConfig.getExpirationTimeInHours(), primaryVerificationToken.getExpiryDate());
        VerificationToken token = prepareToken(account, beginDate, verificationTokenConfig.getHalfExpirationTimeInHours());
        return verificationTokenFacade.create(token);
    }

    private boolean checkIfHalfTimeExceeded(final VerificationToken verificationToken) {
        Date expiryDate = verificationToken.getExpiryDate();

        Date halfTime = dateProvider.subractTimeFromDate(verificationTokenConfig.getHalfExpirationTimeInHours(), expiryDate);
        Date currentTime = dateProvider.currentDate();
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

    private VerificationToken prepareToken(final Account account, final Date beginTime, final double expirationTimeInHours) {
        return VerificationToken.builder()
                .account(account)
                .expiryDate(dateProvider.addTimeToDate(expirationTimeInHours, beginTime))
                .token(generateToken())
                .build();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
