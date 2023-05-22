package pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors;

import jakarta.annotation.Priority;
import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@Log
@TransactionRollbackInterceptor
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 35)
public class TransactionRollbackHandler {

    @AroundInvoke
    public Object intercept(final InvocationContext ctx) {
        try {
            return ctx.proceed();
        } catch (final EJBTransactionRolledbackException e) {
            throw ApplicationBaseException.transactionRollbackException();
        } catch (final Exception e) {
            if (e instanceof ApplicationBaseException app) {
                throw app;
            }
            throw ApplicationBaseException.generalErrorException(e);
        }
    }
}
