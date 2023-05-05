package pl.lodz.p.it.ssbd2023.ssbd06.exceptions;

import static jakarta.ws.rs.core.Response.Status.FORBIDDEN;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import jakarta.ejb.ApplicationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.AccountAlreadyExistException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.AccountDoesNotExistException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.CannotModifyPermissionsException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.IdenticalPasswordsException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.NoMatchingEmailException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.NotActiveAccountException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.NotConfirmedAccountException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.OperationUnsupportedException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.TokenExceededHalfTimeException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.exceptions.UnmatchedPasswordsException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions.AuthenticationException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt.exceptions.NotAuthorizedApplicationException;

@ApplicationException(rollback = true)
public class ApplicationBaseException extends WebApplicationException {

    protected static final String ERROR_UNKNOWN = "ERROR.UNKNOWN";
    protected static final String ERROR_GENERAL_PERSISTENCE = "ERROR.GENERAL_PERSISTENCE";
    protected static final String ERROR_OPTIMISTIC_LOCK = "ERROR.OPTIMISTIC_LOCK";
    protected static final String ERROR_ACCESS_DENIED = "ERROR.ACCESS_DENIED";
    protected static final String ERROR_ACCOUNT_ALREADY_EXIST = "ERROR.ACCOUNT.EXISTS";
    protected static final String ERROR_CANNOT_MODIFY_PERRMISSIONS = "ERROR.CANNOT_MODIFY_PERMISSIONS";
    protected static final String ERROR_FORBIDDEN_OPERATION = "ERROR.FORBIDDEN_OPERATION";
    protected static final String ERROR_IDENTICAL_PASSWORDS = "ERROR.IDENTICAL_PASSWORDS";
    protected static final String ERROR_UNSUPPORTED_OPERATION = "ERROR.UNSUPPORTED_OPERATION";
    protected static final String ERROR_TOKEN_EXCEEDED_HALF_TIME = "ERROR.TOKEN_EXCEEDED_HALF_TIME";
    protected static final String ERROR_TOKEN_NOT_FOUND = "ERROR.TOKEN_NOT_FOUND";
    protected static final String ERROR_ACCOUNT_NOT_FOUND = "ERROR.ACCOUNT_NOT_FOUND";
    protected static final String ERROR_NOT_AUTHORIZED = "ERROR.NOT_AUTHORIZED";
    protected static final String ERROR_AUTHENTICATION = "ERROR.AUTHENTICATION";
    protected static final String ERROR_PASSWORDS_DO_NOT_MATCH = "ERROR.NOT_MATCHING_PASSWORDS";
    protected static final String ERROR_RESOURCE_NOT_FOUND = "ERROR.RESOURCE_NOT_FOUND";

    protected static final String ERROR_EMAIL_DO_NOT_MATCH = "ERROR.NO_MATCHING_EMAILS";
    protected static final String ERROR_ACCOUNT_NOT_ACTIVE = "ERROR.NOT_ACTIVE_ACCOUNT";
    protected static final String ERROR_ACCOUNT_NOT_CONFIRMED = "ERROR.NOT_CONFIRMED_ACCOUNT";


    public ApplicationBaseException(final Response.Status status, final String message) {
        super(Response.status(status).entity(new ErrorResponse(message)).type(MediaType.APPLICATION_JSON_TYPE).build());
    }

    public ApplicationBaseException(final Response.Status status, final String message, final Throwable cause) {
        super(message, cause, Response.status(status).entity(new ErrorResponse(message)).type(MediaType.APPLICATION_JSON_TYPE).build());
    }

    public static ApplicationBaseException generalErrorException(final Throwable cause) {
        return new ApplicationBaseException(INTERNAL_SERVER_ERROR, ERROR_UNKNOWN, cause);
    }

    public static ApplicationBaseException generalErrorException() {
        return new ApplicationBaseException(INTERNAL_SERVER_ERROR, ERROR_UNKNOWN);
    }

    public static ApplicationBaseException persistenceException(final Exception cause) {
        return new ApplicationBaseException(INTERNAL_SERVER_ERROR, ERROR_GENERAL_PERSISTENCE, cause);
    }

    public static ApplicationBaseException accessDeniedException() {
        return new ApplicationBaseException(FORBIDDEN, ERROR_ACCESS_DENIED);
    }

    public static ApplicationOptimisticLockException optimisticLockException() {
        return new ApplicationOptimisticLockException();
    }

    public static AccountAlreadyExistException accountAlreadyExist() {
        return new AccountAlreadyExistException();
    }

    public static CannotModifyPermissionsException cannotModifyPermissionsException() {
        return new CannotModifyPermissionsException();
    }

    public static ForbiddenOperationException forbiddenOperationException() {
        return new ForbiddenOperationException();
    }

    public static IdenticalPasswordsException identicalPasswordsException() {
        return new IdenticalPasswordsException();
    }

    public static OperationUnsupportedException operationUnsupportedException() {
        return new OperationUnsupportedException();
    }

    public static TokenExceededHalfTimeException tokenExceededHalfTimeException() {
        return new TokenExceededHalfTimeException();
    }

    public static UnmatchedPasswordsException unmatchedPasswordsException() {
        return new UnmatchedPasswordsException();
    }

    public static AccountDoesNotExistException accountDoesNotExistException() {
        return new AccountDoesNotExistException();
    }

    public static NotAuthorizedApplicationException notAuthorizedException() {
        return new NotAuthorizedApplicationException();
    }

    public static AuthenticationException authenticationException() {
        return new AuthenticationException();
    }

    public static WebApplicationException resourceNotFoundException() {
        return new ResourceNotFoundException();
    }

    public static NoMatchingEmailException noMatchingEmailException() {
        return new NoMatchingEmailException();
    }

    public static NotActiveAccountException notActiveAccountException() {
        return new NotActiveAccountException();
    }

    public static NotConfirmedAccountException notConfirmedAccountException() {
        return new NotConfirmedAccountException();
    }
}
