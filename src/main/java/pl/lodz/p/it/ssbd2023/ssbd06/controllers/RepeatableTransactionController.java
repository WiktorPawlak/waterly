package pl.lodz.p.it.ssbd2023.ssbd06.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ejb.EJBTransactionRolledbackException;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionAware;

public abstract class RepeatableTransactionController {

    private final Logger log = Logger.getLogger(RepeatableTransactionController.class.getName());

    @Inject
    @Property("transactions.retry-count")
    private int transactionRetryCount;

    @SuppressWarnings("checkstyle:NeedBraces")
    protected <T> T retry(final ContextMethod<T> method, final TransactionAware transactionalBean) {
        int retryCounter = 0;
        boolean markedForRollback;
        T result = null;

        do try {
            result = method.execute();
            markedForRollback = transactionalBean.isLastTransactionRollback();
            if (markedForRollback) {
                log.log(Level.WARNING, "Transaction {0} has been marked for rollback during {1}. Retry: {2}",
                        prepareTransactionInfo(transactionalBean, retryCounter));
            }
        } catch (final EJBTransactionRolledbackException | ApplicationOptimisticLockException e) {
            markedForRollback = true;
            log.log(Level.WARNING, "Transaction {0} has been marked for rollback, because of optimistic lock exception during {1}. Retry: {2}",
                    prepareTransactionInfo(transactionalBean, retryCounter));
            if (retryCounter < 2) {
                throw e;
            }
        } while (markedForRollback && ++retryCounter <= transactionRetryCount);

        if (markedForRollback) {
            throw ApplicationBaseException.transactionRollbackException();
        }

        return result;
    }

    @SuppressWarnings("checkstyle:NeedBraces")
    protected void retry(final ContextProcedure method, final TransactionAware transactionalBean) {
        int retryCounter = 0;
        boolean markedForRollback;

        do try {
            method.execute();
            markedForRollback = transactionalBean.isLastTransactionRollback();
            if (markedForRollback) {
                log.log(Level.WARNING, "Transaction {0} has been marked for rollback during {1}. Retry: {2}",
                        prepareTransactionInfo(transactionalBean, retryCounter));
            }
        } catch (final EJBTransactionRolledbackException | ApplicationOptimisticLockException e) {
            markedForRollback = true;
            log.log(Level.WARNING, "Transaction {0} has been marked for rollback, because of optimistic lock exception during {1}. Retry: {2}",
                    prepareTransactionInfo(transactionalBean, retryCounter));
            if (retryCounter > 2) {
                throw e;
            }
        } while (markedForRollback && ++retryCounter <= transactionRetryCount);

        if (markedForRollback) {
            throw ApplicationBaseException.transactionRollbackException();
        }
    }

    private static Object[] prepareTransactionInfo(final TransactionAware transactionalBean, final int retryCounter) {
        return new Object[]{
                transactionalBean.getTransactionId(),
                transactionalBean.getClass().toGenericString(),
                retryCounter
        };
    }

    @FunctionalInterface
    public interface ContextProcedure {
        void execute() throws ApplicationBaseException;
    }

    @FunctionalInterface
    public interface ContextMethod<T> {
        T execute() throws ApplicationBaseException;
    }
}
