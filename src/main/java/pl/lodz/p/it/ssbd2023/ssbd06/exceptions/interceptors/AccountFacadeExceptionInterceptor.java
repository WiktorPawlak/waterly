package pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors;


import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_ACCOUNT_WITH_EMAIL_ALREADY_EXIST;
import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_ACCOUNT_WITH_LOGIN_ALREADY_EXIST;
import static pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException.ERROR_ACCOUNT_WITH_PHONE_NUMBER_ALREADY_EXIST;

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
@AccountFacadeExceptionHandler
@Interceptor
@Priority(Interceptor.Priority.APPLICATION + 25)
public class AccountFacadeExceptionInterceptor {

    private Map<String, String> accountConstraintsMappings = Map.of("email", ERROR_ACCOUNT_WITH_EMAIL_ALREADY_EXIST,
            "phone_number", ERROR_ACCOUNT_WITH_PHONE_NUMBER_ALREADY_EXIST,
            "login", ERROR_ACCOUNT_WITH_LOGIN_ALREADY_EXIST);

    @AroundInvoke
    public Object intercept(final InvocationContext ctx) throws Exception {
        try {
            return ctx.proceed();
        } catch (final OptimisticLockException ole) {
            throw ApplicationBaseException.optimisticLockException();
        } catch (final PersistenceException | java.sql.SQLException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraintViolationException = (ConstraintViolationException) e.getCause();
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
        Pattern pattern = Pattern.compile("Key \\((.*?)\\)");
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
