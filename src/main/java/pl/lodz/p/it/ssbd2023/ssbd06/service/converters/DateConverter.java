package pl.lodz.p.it.ssbd2023.ssbd06.service.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
}
