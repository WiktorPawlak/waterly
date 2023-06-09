package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import java.text.ParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;

public class DateFormatValidator implements ConstraintValidator<DateFormat, String> {

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    public boolean isValid(final String date, final ConstraintValidatorContext constraintValidatorContext) {
        if (date == null || !date.matches(DATE_PATTERN)) {
            return false;
        }

        try {
            DateConverter.convert(date);
            return true;
        }
        catch (final ParseException e) {
            return false;
        }
    }
}