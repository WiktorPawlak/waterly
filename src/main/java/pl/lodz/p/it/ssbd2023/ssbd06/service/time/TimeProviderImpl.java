package pl.lodz.p.it.ssbd2023.ssbd06.service.time;

import java.util.Calendar;
import java.util.Date;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
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
    public long getDifferenceFromCurrentDateInMillis(final Date givenDate) {
        return givenDate.getTime() - currentDate().getTime();
    }

    private Date prepareDate(final double timeDifferenceInMinutes, final Date beginTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginTime);

        calendar.add(Calendar.MINUTE, (int) (timeDifferenceInMinutes));

        return calendar.getTime();
    }

}
