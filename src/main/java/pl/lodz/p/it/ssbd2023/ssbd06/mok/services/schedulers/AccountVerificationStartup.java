package pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType.REGISTRATION;

import java.util.List;
import java.util.logging.Logger;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExpiredException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.VerificationTokenService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

@Singleton
@Startup
public class AccountVerificationStartup {

    private final Logger log = Logger.getLogger(AccountVerificationStartup.class.getName());

    @Inject
    private AccountService accountService;
    @Inject
    private VerificationTokenService verificationTokenService;
    @Inject
    private AccountVerificationTimer accountVerificationTimer;

    @PostConstruct
    public void initializeVerificationTimers() {
        List<VerificationToken> allTokens = verificationTokenService.findAllTokens();
        for (VerificationToken token : allTokens) {
            long accountId = token.getAccount().getId();
            try {
                accountVerificationTimer.scheduleAccountDeletion(token);
            } catch (final TokenExpiredException e) {
                log.info("Deleting accountId with id: "
                        + accountId
                        + ", because of error occurred during verification token timers initialization: "
                        + e.getMessage());
                verificationTokenService.clearTokens(accountId, REGISTRATION);
                accountService.removeInactiveNotConfirmedAccount(accountId);
            }
        }
    }

}
