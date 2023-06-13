package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static jakarta.enterprise.event.TransactionPhase.AFTER_COMPLETION;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.controllers.RepeatableTransactionProcessor;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.InvoiceCreatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckAddedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class GenerateBillsEndpoint extends RepeatableTransactionProcessor {

    @Inject
    GenerateBillsService generateBillsService;

    public void generateBillsOnInvoiceCreation(@Observes(during = AFTER_COMPLETION) final InvoiceCreatedEvent event) {
        retry(() -> generateBillsService.generateBillsOnInvoiceCreation(event), generateBillsService);
    }

    public void generateBillOnWaterMeterCheckEvent(@Observes(during = AFTER_COMPLETION) final WaterMeterCheckAddedEvent event) {
        retry(() -> generateBillsService.generateBillOnWaterMeterCheckEvent(event), generateBillsService);
    }
}
