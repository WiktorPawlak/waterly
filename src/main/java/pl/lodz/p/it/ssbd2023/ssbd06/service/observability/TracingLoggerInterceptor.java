package pl.lodz.p.it.ssbd2023.ssbd06.service.observability;

import java.util.Arrays;
import java.util.logging.Logger;

import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@Monitored
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 20)
public class TracingLoggerInterceptor {

    private final Logger log = Logger.getLogger(TracingLoggerInterceptor.class.getName());
    private final StringBuilder sb = new StringBuilder();

    @Resource
    private SessionContext sessionContext;

    /**
     * Data Tożsamość NazwaMetody invocated Parametry (id, wersja)
     * Data Tożsamość NazwaMetody returns Parametry (id, wersja)/Wyjątek
     */
    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        try {
            prepareLogPrincipal();
            prepareLogMethodSignature(context);
            prepareLogMethodParameters(context);
            logInfo();

            Object invocationResults = context.proceed();

            prepareLogPrincipal();
            prepareLogInvocationResults(invocationResults);
            logInfo();

            return invocationResults;
        } catch (final Exception e) {
            prepareLogPrincipal();
            prepareLogException(e);
            logSevere();
            throw e;
        }
    }

    private void prepareLogPrincipal() {
        var principal = sessionContext.getCallerPrincipal();
        sb.append("(Principal: ")
                .append(principal == null ? "UNAUTHORIZED" : principal)
                .append(") ");
    }

    private void prepareLogMethodSignature(final InvocationContext context) {
        sb.append(context.getMethod().toGenericString())
                .append(" has been invoked. ");
    }

    private void prepareLogMethodParameters(final InvocationContext context) {
        sb.append("Parameters: ")
                .append(Arrays.toString(context.getParameters()))
                .append(" ");
    }

    private void prepareLogInvocationResults(final Object invocationResults) {
        sb.append(invocationResults == null ? "No return value."
                        : "Returned: Type: " + invocationResults.getClass().toGenericString())
                .append(" with contents: ")
                .append(invocationResults)
                .append(" ");
    }

    private void prepareLogException(final Exception e) {
        sb.append("Exception occurred: ")
                .append(e.getClass().toGenericString())
                .append(", cause: ")
                .append(e.getCause());
    }

    private void logInfo() {
        log.info(sb::toString);
        sb.setLength(0);
    }

    private void logSevere() {
        log.severe(sb::toString);
        sb.setLength(0);
    }
}