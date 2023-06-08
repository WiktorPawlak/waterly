package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@NotNull
@Digits(integer = 8, fraction = 3, message = "VALIDATION.INVALID_STARTING_VALUE")
public @interface StartingValue {

    String message() default "VALIDATION.INVALID_STARTING_VALUE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}