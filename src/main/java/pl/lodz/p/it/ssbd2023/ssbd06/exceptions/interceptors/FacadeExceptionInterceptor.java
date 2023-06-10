package pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors;

import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_ACCOUNT_WITH_EMAIL_ALREADY_EXIST;
import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_ACCOUNT_WITH_LOGIN_ALREADY_EXIST;
import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_ACCOUNT_WITH_PHONE_NUMBER_ALREADY_EXIST;
import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_APARTMENT_WITH_NUMBER_ALREADY_EXIST;
import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_INVOICE_NUMBER_EXISTS;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.exception.ConstraintViolationException;

import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;

@Log
@FacadeExceptionHandler
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 25)
public class FacadeExceptionInterceptor {

    private final Map<String, String> accountConstraintsMappings = Map.of(
            "uk_account_email", ERROR_ACCOUNT_WITH_EMAIL_ALREADY_EXIST,
            "uk_account_phone_number", ERROR_ACCOUNT_WITH_PHONE_NUMBER_ALREADY_EXIST,
            "uk_account_login", ERROR_ACCOUNT_WITH_LOGIN_ALREADY_EXIST,
            "uk_apartment_name", ERROR_APARTMENT_WITH_NUMBER_ALREADY_EXIST,
            "uk_invoice_number", ERROR_INVOICE_NUMBER_EXISTS);

    @AroundInvoke
    public Object intercept(final InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (final OptimisticLockException ole) {
            throw ApplicationBaseException.optimisticLockException();
        } catch (final PersistenceException | java.sql.SQLException e) {
            if (e.getCause() instanceof ConstraintViolationException constraintViolationException) {
                constraintViolationException = (ConstraintViolationException) e.getCause();
                String constraintKey = getConstraintKeyFromException(constraintViolationException.getCause().getMessage());
                throw ApplicationBaseException.persistenceConstraintException(mapConstraintToErrorCode(constraintKey));
            }
            throw ApplicationBaseException.persistenceException(e);
        } catch (final ApplicationBaseException abe) {
            throw abe;
        } catch (final Exception e) {
            throw ApplicationBaseException.generalErrorException(e);
        }
    }

    private String getConstraintKeyFromException(final String string) {
        Pattern pattern = Pattern.compile("constraint\\s+\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            log.severe(() -> "Could not find Key in ConstraintViolationException");
            throw ApplicationBaseException.generalErrorException();
        }
    }

    private String mapConstraintToErrorCode(final String key) {
        return accountConstraintsMappings.get(key);
    }

}
