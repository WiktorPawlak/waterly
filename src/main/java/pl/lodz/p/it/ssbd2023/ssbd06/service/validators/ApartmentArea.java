package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@NotNull
@DecimalMin(value = "1.00")
@DecimalMax(value = "999.99")
public @interface ApartmentArea {

    String message() default "VALIDATION.INVALID_APARTMENT_AREA";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
