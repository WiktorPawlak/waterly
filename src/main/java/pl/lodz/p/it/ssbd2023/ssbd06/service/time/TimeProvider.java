package pl.lodz.p.it.ssbd2023.ssbd06.service.time;

import java.util.Date;

public interface TimeProvider {

    Date addTimeToDate(double timeDifferenceInMinutes, Date beginTime);

    Date subractTimeFromDate(double timeDifferenceInMinutes, Date beginTime);

    Date currentDate();

    long getDifferenceFromCurrentDateInMillis(Date givenDate);

}
