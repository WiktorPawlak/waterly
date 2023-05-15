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
@Size(min = 5, max = 320, message = "VALIDATION.EMAIL_SIZE")
@NotBlank
@Pattern(regexp = ValidationRegex.EMAIL, message = "VALIDATION.EMAIL_PATTERN")
public @interface Email {

    String message() default "VALIDATION.EMAIL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}