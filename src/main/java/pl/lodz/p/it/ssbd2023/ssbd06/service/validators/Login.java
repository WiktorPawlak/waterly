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
@Size(min = 3, max = 36, message = "VALIDATION.LOGIN_SIZE")
@NotBlank
@Pattern(regexp = ValidationRegex.LOGIN, message = "VALIDATION.LOGIN_PATTERN")
public @interface Login {

    String message() default "VALIDATION.LOGIN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
