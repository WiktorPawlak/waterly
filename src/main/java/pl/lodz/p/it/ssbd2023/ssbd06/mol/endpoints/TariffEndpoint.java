package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TariffEndpoint extends TransactionBoundariesTracingEndpoint {
}
