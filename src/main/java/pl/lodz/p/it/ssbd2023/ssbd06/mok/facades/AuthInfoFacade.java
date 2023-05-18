package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AuthInfo;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@Stateless
@FacadeExceptionHandler
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AuthInfoFacade extends AbstractFacade<AuthInfo> {

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public AuthInfoFacade() {
        super(AuthInfo.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }
}
