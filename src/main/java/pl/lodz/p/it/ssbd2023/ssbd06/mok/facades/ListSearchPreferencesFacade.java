package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import java.util.List;
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
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.ListSearchPreferences;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ListSearchPreferencesFacade extends AbstractFacade<ListSearchPreferences> {

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public ListSearchPreferencesFacade() {
        super(ListSearchPreferences.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public ListSearchPreferences create(final ListSearchPreferences entity) {
        return super.create(entity);
    }

    @PermitAll
    public ListSearchPreferences update(final ListSearchPreferences entity) {
        return super.update(entity);
    }

    @PermitAll
    public void delete(final ListSearchPreferences entity) {
        super.delete(entity);
    }

    @PermitAll
    public ListSearchPreferences findById(final Long id) {
        return super.findById(id);
    }

    @PermitAll
    public List<ListSearchPreferences> findAll() {
        return super.findAll();
    }

    @PermitAll
    public Optional<ListSearchPreferences> findByAccount(final Account account) {
        try {
            TypedQuery<ListSearchPreferences> namedQuery = em.createNamedQuery("ListSearchPreferences.findByAccount", ListSearchPreferences.class);
            namedQuery.setFlushMode(FlushModeType.COMMIT);
            namedQuery.setParameter("account", account);
            return Optional.of(namedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }
}
