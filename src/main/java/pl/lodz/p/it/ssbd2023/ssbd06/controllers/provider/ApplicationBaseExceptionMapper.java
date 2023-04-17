package pl.lodz.p.it.ssbd2023.ssbd06.controllers.provider;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.ApplicationBaseException;

@Provider
public class ApplicationBaseExceptionMapper implements ExceptionMapper<ApplicationBaseException> {

    @Override
    public Response toResponse(final ApplicationBaseException exception) {
        return exception.getResponse();
    }

}
