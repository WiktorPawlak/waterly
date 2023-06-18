package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions.DateParseException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;

public class ExpiryDateValidator implements ConstraintValidator<ExpiryDate, String> {

    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    public boolean isValid(final String date, final ConstraintValidatorContext constraintValidatorContext) {
        if (date == null || !date.matches(DATE_PATTERN)) {
            return false;
        }

        try {
            DateConverter.convert(date);
            return true;
        } catch (final DateParseException e) {
            return false;
        }
    }
}