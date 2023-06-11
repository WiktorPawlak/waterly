package pl.lodz.p.it.ssbd2023.ssbd06.service.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateConverter {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static Date convert(final String value) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        return dateFormat.parse(value);
    }

    public static String convert(final Date value) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(value);
    }

    public static Date convertLocalDateToDate(final LocalDate date) {
        LocalDateTime localDateTime = date.atStartOfDay();
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    public static LocalDate convertDateToLocalDate(final String value) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        Date dateToConvert = dateFormat.parse(value);
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static YearMonth convert(final LocalDate date) {
        return YearMonth.of(date.getYear(), date.getMonth());
    }

    public static LocalDate convertInvoiceDate(final String value) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        Date dateToConvert = dateFormat.parse(value + "-01");
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
