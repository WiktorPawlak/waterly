package pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@ServiceExceptionHandler
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 30)
public class ServiceExceptionInterceptor {

    @AroundInvoke
    public Object intercept(final InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (final ApplicationBaseException abe) {
            throw abe;
        } catch (final Exception e) {
            throw ApplicationBaseException.generalErrorException(e);
        }
    }

}
