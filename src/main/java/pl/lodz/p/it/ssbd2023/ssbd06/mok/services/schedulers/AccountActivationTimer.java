package pl.lodz.p.it.ssbd2023.ssbd06.mok.services.schedulers;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class AccountActivationTimer {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Resource
    private TimerService timerService;
    @Inject
    private AccountService accountService;

    @Inject
    @Property("auth.ban.period")
    private int authBanPeriod;

    @Timeout
    @SuppressWarnings("checkstyle:FinalParameters")
    private void execute(Timer timer) {
        long userId = (long) timer.getInfo();
        accountService.changeAccountActiveStatus(userId, true);
        log.info(() -> "Ban period has expired. User with id" + userId + " has been reactivated.");
    }

    public void scheduleAccountActivation(final long id) {
        timerService.createSingleActionTimer(TimeUnit.HOURS.toMillis(authBanPeriod), new TimerConfig(id, true));
        log.info(() -> "Ban period has started for user with id" + id + ". It will last for" + authBanPeriod + " hours.");
    }
}
