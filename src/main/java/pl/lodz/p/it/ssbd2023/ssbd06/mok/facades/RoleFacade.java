package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;

import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@RequestScoped
@FacadeExceptionHandler
public class RoleFacade extends AbstractFacade<Role> {

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public RoleFacade() {
        super(Role.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @RolesAllowed({ADMINISTRATOR})
    public Optional<Role> findRoleByAccountAndPermissionLevel(final Account account, final String permissionLevel) {
        TypedQuery<Role> accountTypedQuery = em.createNamedQuery("Role.findByAccountAndPermissionLevel", Role.class);
        accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
        accountTypedQuery.setParameter("account", account);
        accountTypedQuery.setParameter("permissionLevel", permissionLevel);
        try {
            return Optional.of(accountTypedQuery.getSingleResult());
        } catch (final Exception e) {
            return Optional.empty();
        }
    }

}
