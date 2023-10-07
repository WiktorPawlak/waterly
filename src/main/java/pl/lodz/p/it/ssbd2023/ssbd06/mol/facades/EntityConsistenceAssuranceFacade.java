package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.ConsistencyAssuranceTopic;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.EntityConsistenceAssurance;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@RequestScoped
public class EntityConsistenceAssuranceFacade extends AbstractFacade<EntityConsistenceAssurance> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public EntityConsistenceAssuranceFacade() {
        super(EntityConsistenceAssurance.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EntityConsistenceAssurance create(final EntityConsistenceAssurance entity) {
        return super.create(entity);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Optional<EntityConsistenceAssurance> findConsistencyAssuranceByTopic(final ConsistencyAssuranceTopic topic) {
        try {
            return Optional.of(em.createNamedQuery("EntityConsistenceAssurance.findByTopic", EntityConsistenceAssurance.class)
                    .setFlushMode(FlushModeType.COMMIT)
                    .setParameter("topic", topic)
                    .getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Optional<EntityConsistenceAssurance> findConsistencyAssuranceByTopicAndUniqueChain(final ConsistencyAssuranceTopic topic, final String uniqueChain) {
        try {
            return Optional.of(em.createNamedQuery("EntityConsistenceAssurance.findByTopicAndUniqueChain", EntityConsistenceAssurance.class)
                    .setFlushMode(FlushModeType.COMMIT)
                    .setParameter("topic", topic)
                    .setParameter("uniqueChain", uniqueChain)
                    .getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }


}
