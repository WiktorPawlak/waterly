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
import jakarta.validation.constraints.Size;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Size(min = 2, max = 50, message = "VALIDATION.LAST_NAME_SIZE")
@NotBlank
@Pattern(regexp = ValidationRegex.LAST_NAME, message = "VALIDATION.LAST_NAME_PATTERN")
public @interface LastName {

    String message() default "VALIDATION.LAST_NAME";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
