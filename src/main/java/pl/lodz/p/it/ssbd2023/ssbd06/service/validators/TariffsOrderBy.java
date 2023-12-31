package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {})
@Retention(RUNTIME)
@Pattern(regexp = ValidationRegex.TARIFF_ORDER_BY, message = "VALIDATION.TARIFFS_INVALID_ORDERBY")
public @interface TariffsOrderBy {
    String message() default "VALIDATION.TARIFFS_INVALID_ORDERBY";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
