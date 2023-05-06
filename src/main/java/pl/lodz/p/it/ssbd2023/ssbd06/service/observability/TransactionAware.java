package pl.lodz.p.it.ssbd2023.ssbd06.service.observability;

public interface TransactionAware {

    boolean isLastTransactionRollback();

    String getTransactionId();
}
