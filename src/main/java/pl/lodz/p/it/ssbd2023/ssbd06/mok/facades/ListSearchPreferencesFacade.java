package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
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
@RequestScoped
public class ListSearchPreferencesFacade extends AbstractFacade<ListSearchPreferences> {

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public ListSearchPreferencesFacade() {
        super(ListSearchPreferences.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
    public ListSearchPreferences create(final ListSearchPreferences entity) {
        return super.create(entity);
    }

    @Override
    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
    public ListSearchPreferences update(final ListSearchPreferences entity) {
        return super.update(entity);
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER, OWNER})
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
