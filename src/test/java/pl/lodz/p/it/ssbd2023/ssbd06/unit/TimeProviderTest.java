package pl.lodz.p.it.ssbd2023.ssbd06.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProviderImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimeProviderTest {

    private static final long FRI_13_FEB_2009 = 1234567890000L;
    private static final long TUE_13_JAN_2009 = 1231866860000L;
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

    @ParameterizedTest(name = "month = {0}, remaining days = {1}")
    @MethodSource("provideDates")
    void shouldReturnRemainingDaysInEvenMonth(final LocalDate month, long remainingDays) {
        //when
        long resultRemainingDays = timeProvider.getDaysRemainingInMonth(month);

        //then
        assertEquals(remainingDays, resultRemainingDays);
    }

    private static Stream<Arguments> provideDates() {
        return Stream.of(
                Arguments.of(Named.of("Even month", LocalDate.of(2023, 6, 1)), 29),
                Arguments.of(Named.of("Odd month", LocalDate.of(2023, 7, 1)), 30)
        );
    }

    @Test
    void shouldReturnDifferenceBetweenDatesInDays() {
        //given
        LocalDate minuendDate = LocalDate.of(2023, 6, 17);
        LocalDate subtrahendDate = LocalDate.of(2023, 5, 15);

        //when
        long difference = timeProvider.getDifferenceBetweenDatesInDays(minuendDate, subtrahendDate);

        //then
        assertEquals(33, difference);
    }

    @Test
    void shouldCheckIfFirstDateIsBeforeSecondOne() {
        //when
        var result = timeProvider.checkDateIsBeforeOtherDate(Instant.ofEpochMilli(TUE_13_JAN_2009), Instant.ofEpochMilli(FRI_13_FEB_2009));

        //then
        assertTrue(result);
    }
}