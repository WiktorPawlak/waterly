package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@NotNull
@Size(min = 1, max = 20, message = "VALIDATION.APARTMENT_NUMBER_SIZE")
@NotBlank
@Pattern(regexp = ValidationRegex.APARTMENT_NUMBER, message = "VALIDATION.APARTMENT_NUMBER_SIZE")
public @interface ApartmentNumber {

    String message() default "VALIDATION.INVALID_APARTMENT_NUMBER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
