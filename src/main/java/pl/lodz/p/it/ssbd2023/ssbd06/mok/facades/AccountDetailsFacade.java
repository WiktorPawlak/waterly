package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@RequestScoped
@FacadeExceptionHandler
public class AccountDetailsFacade extends AbstractFacade<AccountDetails> {

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public AccountDetailsFacade() {
        super(AccountDetails.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }
}
