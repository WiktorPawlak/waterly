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
@Size(min = 8, max = 9, message = "minimal 8 characters and maximal 9 characters")
@NotBlank
@Pattern(regexp = ValidationRegex.PHONE_NUMBER, message = "must contain only numbers")
public @interface PhoneNumber {

    String message() default "Tel number is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}