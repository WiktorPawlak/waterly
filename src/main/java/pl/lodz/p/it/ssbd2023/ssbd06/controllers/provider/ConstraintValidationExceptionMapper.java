package pl.lodz.p.it.ssbd2023.ssbd06.controllers.provider;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ValidationErrorDetails;

@Provider
public class ConstraintValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violation = exception.getConstraintViolations();
        return Response.status(BAD_REQUEST).entity(violation.stream()
                        .map(it -> new ValidationErrorDetails(extractFieldName(it.getPropertyPath()), it.getMessage()))
                        .toList())
                .build();
    }

    private String extractFieldName(final Path path) {
        String[] pathParts = path.toString().split("\\.");
        return pathParts[pathParts.length - 1];
    }

}
