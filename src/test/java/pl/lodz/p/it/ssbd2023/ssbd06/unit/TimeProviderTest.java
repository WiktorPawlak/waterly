package pl.lodz.p.it.ssbd2023.ssbd06.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProviderImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimeProviderTest {

    private static final long FRI_13_FEB_2009 = 1234567890000L;
    private static final int MILLIS_IN_MINUTE = 60000;

    @Spy
    private final TimeProvider timeProvider = new TimeProviderImpl();

    @BeforeAll
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddTimeToDate() {
        // given
        Date beginTime = new Date(FRI_13_FEB_2009);
        double timeDifferenceInMinutes = 120;

        // when
        Date preparedDate = timeProvider.addTimeToDate(timeDifferenceInMinutes, beginTime);

        // then
        assertEquals(new Date((long) (FRI_13_FEB_2009 + timeDifferenceInMinutes * MILLIS_IN_MINUTE)), preparedDate);
    }

    @Test
    void shouldSubtractTimeFromDate() {
        // given
        Date beginTime = new Date(FRI_13_FEB_2009);
        double timeDifferenceInMinutes = 120;

        // when
        Date preparedDate = timeProvider.subractTimeFromDate(timeDifferenceInMinutes, beginTime);

        // then
        assertEquals(new Date((long) (FRI_13_FEB_2009 - timeDifferenceInMinutes * MILLIS_IN_MINUTE)), preparedDate);
    }

    @Test
    void shouldGetDifferenceFromCurrentDateInMillis() {
        // given
        Date givenTime = new Date(FRI_13_FEB_2009);

        when(timeProvider.currentDate()).thenReturn(new Date(FRI_13_FEB_2009 - MILLIS_IN_MINUTE));

        // when
        long differenceFromCurrentDateInMillis = timeProvider.getDifferenceFromCurrentDateInMillis(givenTime);

        // then
        assertEquals(MILLIS_IN_MINUTE, differenceFromCurrentDateInMillis);
    }
}