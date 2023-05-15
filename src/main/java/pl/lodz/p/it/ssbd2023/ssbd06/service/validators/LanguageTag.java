package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@NotBlank
@Pattern(regexp = ValidationRegex.LANGUAGE_TAG, message = "VALIDATION.LANGUAGE_TAG_PATTERN")
public @interface LanguageTag {

    String message() default "VALIDATION.LANGUAGE_TAG";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
