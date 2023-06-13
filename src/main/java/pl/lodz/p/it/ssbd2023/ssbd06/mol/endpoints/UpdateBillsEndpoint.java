package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static jakarta.enterprise.event.TransactionPhase.AFTER_COMPLETION;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionProcessor;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.TariffUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.UpdateBillsService;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class UpdateBillsEndpoint extends RepeatableTransactionProcessor {

    @Inject
    UpdateBillsService updateBillsService;

    public void updateBillsOnInvoiceUpdate(@Observes(during = AFTER_COMPLETION) final InvoiceUpdatedEvent event) {
        retry(() -> updateBillsService.updateBillsOnInvoiceUpdate(event), updateBillsService);
    }

    public void updateBillsOnTariffUpdate(@Observes(during = AFTER_COMPLETION) final TariffUpdatedEvent event) {
        retry(() -> updateBillsService.updateBillsOnTariffUpdate(event), updateBillsService);
    }

}
