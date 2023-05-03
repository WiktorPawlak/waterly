package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import java.util.logging.Logger;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@Stateless
@FacadeExceptionHandler
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountDetailsFacade extends AbstractFacade<AccountDetails> {

    private final Logger log = Logger.getLogger(getClass().getName());

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public AccountDetailsFacade() {
        super(AccountDetails.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
