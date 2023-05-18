package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Pattern(regexp = ValidationRegex.TWO_FA_CODE, message = "VALIDATION.INCORRECT_TWO_FA_CODE")
public @interface TwoFACode {
    String message() default "VALIDATION.INCORRECT_TWO_FA_CODE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
