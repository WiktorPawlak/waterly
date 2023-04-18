package pl.lodz.p.it.ssbd2023.ssbd06.controllers.provider;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.response.ErrorResponse;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    public Response toResponse(final ForbiddenException exception) {
        return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("No access to the resource")).build();
    }

}
