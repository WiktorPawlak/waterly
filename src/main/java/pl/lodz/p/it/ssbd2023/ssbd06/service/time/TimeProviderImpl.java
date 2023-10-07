package pl.lodz.p.it.ssbd2023.ssbd06.service.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@RequestScoped
@PermitAll
public class TimeProviderImpl implements TimeProvider {

    @Override
    public Date addTimeToDate(final double timeDifferenceInMinutes, final Date beginTime) {
        return prepareDate(timeDifferenceInMinutes, beginTime);
    }

    @Override
    public Date subractTimeFromDate(final double timeDifferenceInMinutes, final Date beginTime) {
        return prepareDate(-timeDifferenceInMinutes, beginTime);
    }

    @Override
    public Date currentDate() {
        return new Date();
    }

    @Override
    public LocalDate currentLocalDate() {
        return LocalDate.now();
    }

    @Override
    public long getDifferenceFromCurrentDateInMillis(final Date givenDate) {
        return givenDate.getTime() - currentDate().getTime();
    }

    @Override
    public long getDifferenceBetweenDatesInDays(final LocalDate minuend, final LocalDate subtrahend) {
        return Math.abs(ChronoUnit.DAYS.between(subtrahend, minuend));
    }

    @Override
    public long getDaysRemainingInMonth(final LocalDate date) {
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        return ChronoUnit.DAYS.between(date, lastDayOfMonth);
    }

    @Override
    public boolean checkDateIsBeforeOtherDate(final Instant date, final Instant otherDate) {
        return date.isBefore(otherDate);
    }

    @Override
    public long getDaysNumberInCurrentMonth() {
        return YearMonth.now().lengthOfMonth();
    }

    private Date prepareDate(final double timeDifferenceInMinutes, final Date beginTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginTime);

        calendar.add(Calendar.MINUTE, (int) (timeDifferenceInMinutes));

        return calendar.getTime();
    }

}
