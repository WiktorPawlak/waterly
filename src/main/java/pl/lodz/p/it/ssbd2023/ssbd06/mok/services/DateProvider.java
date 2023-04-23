package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import java.util.Calendar;
import java.util.Date;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DateProvider {

    private static final int MINUTES_IN_HOUR = 60;

    @PermitAll
    public Date addTimeToDate(final double timeDifferenceInHours, final Date beginTime) {
        return prepareDate(timeDifferenceInHours, beginTime);
    }
    @PermitAll
    public Date subractTimeFromDate(final double timeDifferenceInHours, final Date beginTime) {
        return prepareDate(-timeDifferenceInHours, beginTime);
    }

    @PermitAll
    public Date currentDate() {
        return new Date();
    }

    @PermitAll
    public long getDifferenceFromCurrentDateInMillis(final Date givenDate) {
        return givenDate.getTime() - currentDate().getTime();
    }

    private Date prepareDate(final double timeDifferenceInHours, final Date beginTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginTime);

        calendar.add(Calendar.MINUTE, (int) (timeDifferenceInHours * MINUTES_IN_HOUR));

        return calendar.getTime();
    }

}
