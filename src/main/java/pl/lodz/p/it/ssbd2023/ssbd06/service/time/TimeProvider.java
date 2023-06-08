package pl.lodz.p.it.ssbd2023.ssbd06.service.time;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

public interface TimeProvider {

    Date addTimeToDate(double timeDifferenceInMinutes, Date beginTime);

    Date subractTimeFromDate(double timeDifferenceInMinutes, Date beginTime);

    Date currentDate();

    LocalDate currentLocalDate();

    long getDifferenceFromCurrentDateInMillis(Date givenDate);

    long getDifferenceBetweenDatesInDays(LocalDate minuend, LocalDate subtrahend);

    long getDaysRemainingInMonth(LocalDate date);

    boolean checkDateIsBeforeOtherDate(Instant date, Instant otherDate);
}
