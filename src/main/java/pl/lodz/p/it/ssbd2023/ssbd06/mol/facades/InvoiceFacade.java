package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class InvoiceFacade extends AbstractFacade<Invoice> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public InvoiceFacade() {
        super(Invoice.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }
}
