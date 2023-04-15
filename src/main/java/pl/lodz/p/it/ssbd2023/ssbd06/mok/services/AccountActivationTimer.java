package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.Timeout;
import jakarta.ejb.Timer;
import jakarta.ejb.TimerConfig;
import jakarta.ejb.TimerService;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Slf4j
@Stateless
public class AccountActivationTimer {

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
        log.info("Ban period has expired. User with id {} has been reactivated.", userId);
    }

    public void scheduleAccountActivation(final long id) {
        timerService.createSingleActionTimer(TimeUnit.HOURS.toMillis(authBanPeriod), new TimerConfig(id, true));
        log.info("Ban period has started for user with id {}. It will last for {} hours.", id, authBanPeriod);
    }
}
