package pl.lodz.p.it.ssbd2023.ssbd06.controllers.provider;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.auth.ErrorResponse;

@Provider
public class ConstraintValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violation = exception.getConstraintViolations();
        return Response.status(BAD_REQUEST).entity(new ErrorResponse(violation.stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()))
                .build();
    }

}
