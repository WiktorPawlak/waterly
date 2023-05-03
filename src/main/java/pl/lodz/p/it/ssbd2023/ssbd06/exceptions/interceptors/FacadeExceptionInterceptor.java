package pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@FacadeExceptionHandler
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 25)
public class FacadeExceptionInterceptor {

    @AroundInvoke
    public Object intercept(final InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (final OptimisticLockException ole) {
            throw ApplicationBaseException.optimisticLockException();
        } catch (final PersistenceException | java.sql.SQLException e) {
            throw ApplicationBaseException.persistenceException(e);
        } catch (final ApplicationBaseException abe) {
            throw abe;
        } catch (final Exception e) {
            throw ApplicationBaseException.generalErrorException(e);
        }
    }
}
