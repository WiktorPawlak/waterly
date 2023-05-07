package pl.lodz.p.it.ssbd2023.ssbd06.service.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Range(min = 1, max = Integer.MAX_VALUE)
public @interface Page {

    String message() default "validation.invalid_page_number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
