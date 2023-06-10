package pl.lodz.p.it.ssbd2023.ssbd06.service.observability;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import jakarta.annotation.Priority;
import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;

@Log
@Monitored
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 20)
public class TracingLoggerInterceptor {

    @Inject
    @Property("system.log.stackTraces")
    private boolean shouldLogStackTraces;

    private final StringBuilder sb = new StringBuilder();

    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        try {
            prepareLogPrincipal();
            prepareLogMethodSignature(context);
            prepareLogMethodParameters(context);
            logInfo();

            Object invocationResults = context.proceed();

            prepareLogPrincipal();
            prepareLogMethodSignature(context);
            prepareLogInvocationResults(invocationResults);
            logInfo();

            return invocationResults;
        } catch (final Exception e) {
            prepareLogPrincipal();
            prepareLogMethodSignature(context);
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
        if (invocationResults == null) {
            sb.append("No return value.");
        } else {
            sb.append("Returned: Type: ")
                    .append(invocationResults.getClass().toGenericString())
                    .append(" with contents: ")
                    .append(invocationResults)
                    .append(" ");
        }
    }

    private void prepareLogException(final Exception e) {
        sb.append("Exception occurred: ")
                .append(e.getClass().toGenericString())
                .append(", cause: ")
                .append(e instanceof ApplicationBaseException abe ?
                        abe.getResponse().getEntity().toString()
                        : e.getCause());
        if (shouldLogStackTraces) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            sb.append(", stackTrace: ")
                    .append(stackTrace);
        }
    }

    private void logInfo() {
        log.info(sb::toString);
        clearStringBuilderBuffer();
    }

    private void logSevere() {
        log.severe(sb::toString);
        clearStringBuilderBuffer();
    }

    private void clearStringBuilderBuffer() {
        sb.setLength(0);
    }
}