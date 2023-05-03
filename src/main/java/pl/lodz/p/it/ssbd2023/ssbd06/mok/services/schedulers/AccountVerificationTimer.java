package pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers;

import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExpiredException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.DateProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.VerificationTokenService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

@ServiceExceptionHandler
@Stateless
public class AccountVerificationTimer {

    private static final String TIMER_NAME_PREFIX = "VERIFICATION_";

    private final Logger log = Logger.getLogger(getClass().getName());

    @Resource
    private TimerService timerService;
    @Inject
    private AccountService accountService;
    @Inject
    private VerificationTokenService verificationTokenService;
    @Inject
    private DateProvider dateProvider;

    @Timeout
    @SuppressWarnings("checkstyle:FinalParameters")
    private void execute(Timer timer) {
        long accountId = retrieveAccountId((String) timer.getInfo());
        accountService.removeInactiveNotConfirmedAccount(accountId);
        verificationTokenService.clearTokens(accountId);
        log.info("Account deletion period has expired. Account with id " + accountId + " has been deleted.");
    }

    public void scheduleAccountDeletion(final VerificationToken token) throws TokenExpiredException {
        long accountId = token.getAccount().getId();
        String timerName = prepareTimerName(accountId);
        long expirationTimeInMillis = calculateExpirationTimeInMillis(token.getExpiryDate());
        timerService.createSingleActionTimer(expirationTimeInMillis, new TimerConfig(timerName, true));
        log.info("Account " + accountId + " will be deleted in " + expirationTimeInMillis + "ms unless it is verified with token");
    }

    public void cancelAccountDeletion(final long id) {
        String timerName = prepareTimerName(id);
        timerService.getTimers().stream()
                .filter(it -> Objects.equals(it.getInfo(), timerName))
                .forEach(it -> {
                    it.cancel();
                    log.info("Account " + id + " scheduled deletion canceled");
                });
    }

    private String prepareTimerName(final long accountId) {
        return TIMER_NAME_PREFIX + accountId;
    }

    private long retrieveAccountId(final String timerName) {
        return Integer.parseInt(timerName.substring(TIMER_NAME_PREFIX.length()));
    }

    private long calculateExpirationTimeInMillis(final Date expiryDate) throws TokenExpiredException {
        if (expiryDate.before(dateProvider.currentDate())) {
            throw new TokenExpiredException();
        }
        return dateProvider.getDifferenceFromCurrentDateInMillis(expiryDate);
    }
}
