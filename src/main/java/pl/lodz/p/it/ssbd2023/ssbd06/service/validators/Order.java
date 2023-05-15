package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {})
@Retention(RUNTIME)
@NotNull(message = "VALIDATION.ORDER_NULL")
@Pattern(regexp = ValidationRegex.ORDER, message = "VALIDATION.ORDER_PATTERN")
public @interface Order {
    String message() default "VALIDATION.ORDER_INVALID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
