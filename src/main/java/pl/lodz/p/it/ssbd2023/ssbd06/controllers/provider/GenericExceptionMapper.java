package pl.lodz.p.it.ssbd2023.ssbd06.controllers.provider;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ejb.AccessLocalException;
import jakarta.ejb.EJBAccessException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final String UNKNOWN_EXCEPTION_MESSAGE = "ERROR.UNKNOWN";
    Logger logger = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(final Throwable throwable) {
        try {
            throw throwable;
        } catch (final ApplicationBaseException appBaseException) {
            return appBaseException.getResponse();
        } catch (final EJBAccessException | AccessLocalException ae) {
            return ApplicationBaseException.accessDeniedException().getResponse();
        } catch (final NotFoundException nfe) {
            return ApplicationBaseException.resourceNotFoundException().getResponse();
        } catch (final ForbiddenException e) {
            return ApplicationBaseException.forbiddenOperationException().getResponse();
        } catch (final NotAuthorizedException e) {
            return ApplicationBaseException.notAuthorizedException().getResponse();
        } catch (final Throwable t) {
            logger.log(Level.SEVERE, UNKNOWN_EXCEPTION_MESSAGE, throwable);
            return ApplicationBaseException.generalErrorException(t).getResponse();
        }
    }
}
