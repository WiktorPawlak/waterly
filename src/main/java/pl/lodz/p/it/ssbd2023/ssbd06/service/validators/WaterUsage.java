package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Payload;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Digits(integer = 10, fraction = 3)
@NotNull
public @interface WaterUsage {
    String message() default "VALIDATION.WATER_USAGE_INVALID_PRECISION";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
