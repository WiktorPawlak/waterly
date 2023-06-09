package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = ExpiryDateValidator.class)
public @interface ExpiryDate {

    String message() default "VALIDATION.EXPIRY_DATE_PATTERN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
