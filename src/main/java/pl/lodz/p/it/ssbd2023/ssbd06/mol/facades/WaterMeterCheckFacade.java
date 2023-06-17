package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WaterMeterCheckFacade extends AbstractFacade<WaterMeterCheck> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public WaterMeterCheckFacade() {
        super(WaterMeterCheck.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterMeterCheck create(final WaterMeterCheck entity) {
        return super.create(entity);
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterMeterCheck update(final WaterMeterCheck entity) {
        return super.update(entity);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<WaterMeterCheck> findWaterMeterCheckByDateAndWaterMeterId(final LocalDate month, final Long waterMeterId) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<WaterMeterCheck> criteriaQuery = criteriaBuilder.createQuery(WaterMeterCheck.class);
        Root<WaterMeterCheck> root = criteriaQuery.from(WaterMeterCheck.class);

        Join<WaterMeterCheck, WaterMeter> waterMeterJoin = root.join("waterMeter");
        criteriaQuery.select(root);

        Expression<Integer> monthExpression = criteriaBuilder.function(
                "DATE_PART",
                Integer.class,
                criteriaBuilder.literal("MONTH"),
                root.get("checkDate")
        );
        Predicate monthPredicate = criteriaBuilder.equal(monthExpression, month.getMonthValue());

        Predicate waterMeterIdPredicate = criteriaBuilder.equal(waterMeterJoin.get("id"), waterMeterId);
        Predicate finalPredicate = criteriaBuilder.and(monthPredicate, waterMeterIdPredicate);

        criteriaQuery.where(finalPredicate);

        try {
            return Optional.of(em.createQuery(criteriaQuery).getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Optional<WaterMeterCheck> findChecksForDateAndWaterMeterType(final WaterMeterType type, LocalDate date) {
        try {
            return Optional.of(em.createNamedQuery("WaterMeterCheck.findCheckByDateAndWaterMeterType", WaterMeterCheck.class)
                    .setFlushMode(FlushModeType.COMMIT)
                    .setParameter("waterMeterType", type)
                    .setParameter("year", date.getYear())
                    .setParameter("month", date.getMonthValue())
                    .getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }
}
