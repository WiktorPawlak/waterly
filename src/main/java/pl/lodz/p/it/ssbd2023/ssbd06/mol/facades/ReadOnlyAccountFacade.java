package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ReadOnlyAccountFacade extends AbstractFacade<Account> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public ReadOnlyAccountFacade() {
        super(Account.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    public Optional<Account> findByLogin(final String identity) {
        try {
            TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findByLogin", Account.class);
            accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
            accountTypedQuery.setParameter("login", identity);
            return Optional.of(accountTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

}
