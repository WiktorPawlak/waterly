package pl.lodz.p.it.ssbd2023.ssbd06.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.DateProvider;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DateProviderTest {

    private static final long FRI_13_FEB_2009 = 1234567890000L;
    private static final int MILLIS_IN_HOUR = 3600000;

    @Spy
    private final DateProvider dateProvider = new DateProvider();

    @BeforeAll
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddTimeToDate() {
        // given
        Date beginTime = new Date(FRI_13_FEB_2009);
        double timeDifferenceInHours = 2;

        // when
        Date preparedDate = dateProvider.addTimeToDate(timeDifferenceInHours, beginTime);

        // then
        assertEquals(preparedDate, new Date((long) (FRI_13_FEB_2009 + timeDifferenceInHours * MILLIS_IN_HOUR)));
    }

    @Test
    void shouldSubtractTimeFromDate() {
        // given
        Date beginTime = new Date(FRI_13_FEB_2009);
        double timeDifferenceInHours = 2;

        // when
        Date preparedDate = dateProvider.subractTimeFromDate(timeDifferenceInHours, beginTime);

        // then
        assertEquals(preparedDate, new Date((long) (FRI_13_FEB_2009 - timeDifferenceInHours * MILLIS_IN_HOUR)));
    }

    @Test
    void shouldGetDifferenceFromCurrentDateInMillis() {
        // given
        Date givenTime = new Date(FRI_13_FEB_2009);

        when(dateProvider.currentDate()).thenReturn(new Date(FRI_13_FEB_2009 - MILLIS_IN_HOUR));

        // when
        long differenceFromCurrentDateInMillis = dateProvider.getDifferenceFromCurrentDateInMillis(givenTime);

        // then
        assertEquals(MILLIS_IN_HOUR, differenceFromCurrentDateInMillis);
    }
}