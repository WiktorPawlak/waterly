package pl.lodz.p.it.ssbd2023.ssbd06.service.observability;

import static jakarta.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.ejb.SessionContext;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.TransactionAttribute;

public abstract class TransactionBoundariesTracingEndpoint implements SessionSynchronization {

    private final Logger log = Logger.getLogger(TransactionBoundariesTracingEndpoint.class.getName());

    private String transactionId;

    private boolean lastTransactionRollback;

    @Resource
    private SessionContext sessionContext;

    @TransactionAttribute(NOT_SUPPORTED)
    public boolean isLastTransactionRollback() {
        return lastTransactionRollback;
    }

    @Override
    public void afterBegin() throws EJBException {
        transactionId = Long.toString(System.currentTimeMillis())
                + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        log.log(Level.INFO, "Transaction id={0} started by {1}, principal: {2}",
                new Object[]{
                        transactionId,
                        this.getClass().getName(),
                        sessionContext.getCallerPrincipal().getName()
                });
    }

    @Override
    public void beforeCompletion() throws EJBException {
        log.log(Level.INFO, "Transaction id={0} before {1} completion, principal: {2}",
                new Object[]{
                        transactionId,
                        this.getClass().getName(),
                        sessionContext.getCallerPrincipal().getName()
                });
    }

    @Override
    public void afterCompletion(final boolean committed) throws EJBException {
        lastTransactionRollback = !committed;
        log.log(Level.INFO, "Transaction id={0} ended in {1} with result: {3}, principal {2}",
                new Object[]{
                        transactionId,
                        this.getClass().getName(),
                        sessionContext.getCallerPrincipal().getName(),
                        committed ? "ACCEPT" : "ROLLBACK"
                });
    }
}
