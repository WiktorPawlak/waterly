package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import static java.util.Calendar.HOUR_OF_DAY;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.VerificationTokenFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AllArgsConstructor
@NoArgsConstructor
public class VerificationTokenService {

    @Inject
    private VerificationTokenFacade verificationTokenFacade;

    @Inject
    @Property("verification.token.expirationTimeInHours")
    private String expirationTimeInHours;

    @PermitAll
    public boolean verifyTokenAffiliation(String token, String login) {
        VerificationToken verificationToken = verificationTokenFacade.findByToken(token);
        return Objects.equals(verificationToken.getAccount().getLogin(), login);
    }

    @PermitAll
    public VerificationToken createToken(Account account) {
        VerificationToken token = VerificationToken.builder()
                .account(account)
                .expiryDate(prepareExpirationDate())
                .token(generateToken())
                .build();

        return verificationTokenFacade.create(token);
    }

    private Date prepareExpirationDate() {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(HOUR_OF_DAY, Integer.parseInt(expirationTimeInHours));

        return calendar.getTime();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
