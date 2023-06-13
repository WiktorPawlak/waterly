package pl.lodz.p.it.ssbd2023.ssbd06.controllers;

import java.util.logging.Level;

import jakarta.inject.Inject;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.TransactionRollbackException;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionAware;

@Log
public abstract class RepeatableTransactionProcessor {

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
                logMarkedForRollback(transactionalBean, retryCounter);
            }
        } catch (final TransactionRollbackException | ApplicationOptimisticLockException e) {
            markedForRollback = true;
            logMarkedForRollback(transactionalBean, retryCounter);
            if (retryCounter > 2) {
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
                logMarkedForRollback(transactionalBean, retryCounter);
            }
        } catch (final TransactionRollbackException | ApplicationOptimisticLockException e) {
            markedForRollback = true;
            logMarkedForRollback(transactionalBean, retryCounter);
            if (retryCounter > 2) {
                throw e;
            }
        } while (markedForRollback && ++retryCounter <= transactionRetryCount);

        if (markedForRollback) {
            throw ApplicationBaseException.transactionRollbackException();
        }
    }

    private void logMarkedForRollback(final TransactionAware transactionalBean, final int retryCounter) {
        log.log(Level.WARNING, "Transaction {0} has been marked for rollback during {1}. Retry: {2}",
                prepareTransactionInfo(transactionalBean, retryCounter));
    }

    private Object[] prepareTransactionInfo(final TransactionAware transactionalBean, final int retryCounter) {
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
