package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.Date;
import java.util.List;
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
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WaterMeterFacade extends AbstractFacade<WaterMeter> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public WaterMeterFacade() {
        super(WaterMeter.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterMeter findById(final Long id) {
        return super.findById(id);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public List<WaterMeter> findAllActiveByType(final WaterMeterType type) {
        return em.createNamedQuery("WaterMeter.findAllActiveByType", WaterMeter.class)
                .setFlushMode(FlushModeType.COMMIT)
                .setParameter("type", type)
                .getResultList();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Optional<WaterMeter> findOneActiveByType(final WaterMeterType type) {
        try {
            return Optional.of(em.createNamedQuery("WaterMeter.findAllActiveByType", WaterMeter.class)
                    .setFlushMode(FlushModeType.COMMIT)
                    .setParameter("type", type)
                    .getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @RolesAllowed(FACILITY_MANAGER)
    @Override
    public WaterMeter create(final WaterMeter entity) {
        return super.create(entity);
    }

    @RolesAllowed(FACILITY_MANAGER)
    @Override
    public WaterMeter update(final WaterMeter entity) {
        return super.update(entity);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<WaterMeter> findWaterMeters(final String pattern,
                                            final int page,
                                            final int pageSize,
                                            final boolean ascOrder,
                                            final String orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<WaterMeter> query = cb.createQuery(WaterMeter.class);
        Root<WaterMeter> waterMeterRoot = query.from(WaterMeter.class);

        if (ascOrder) {
            query.orderBy(cb.asc(resolveFieldClass(waterMeterRoot, orderBy).get(orderBy)));
        } else {
            query.orderBy(cb.desc(resolveFieldClass(waterMeterRoot, orderBy).get(orderBy)));
        }

        Predicate predicate = cb.conjunction();

        if (pattern != null) {
            predicate = cb.and(predicate, cb.or(getFilterByPatternPredicates(pattern, cb, waterMeterRoot)));
        }

        query.where(predicate);

        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    private From<?, ?> resolveFieldClass(final Root<WaterMeter> root, final String fieldName) {
        return switch (fieldName) {
            case "expiryDate", "expectedDailyUsage", "startingValue", "type", "apartment" -> root;
            default -> {
                log.severe(() -> "Error, trying to query by invalid field");
                throw ApplicationBaseException.generalErrorException();
            }
        };
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Long countAll(final String pattern) {
        return count(pattern);
    }

    private Long count(final String pattern) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<WaterMeter> waterMeter = query.from(WaterMeter.class);

        Predicate predicate = cb.conjunction();

        if (pattern != null) {
            predicate = cb.and(predicate, cb.or(getFilterByPatternPredicates(pattern, cb, waterMeter)));
        }

        query.where(predicate);
        query.select(cb.count(waterMeter));

        return em.createQuery(query).getSingleResult();
    }

    private Predicate[] getFilterByPatternPredicates(final String pattern, final CriteriaBuilder cb, final Root<WaterMeter> waterMeter) {
        String filterPattern = "%" + pattern.toUpperCase() + "%";

        Predicate numberPredicate = cb.like(cb.upper(waterMeter.get("serialNumber")), filterPattern);

        return new Predicate[]{numberPredicate};
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<WaterMeter> findAllByApartmentId(final long apartmentId, final Date currentDate) {
//        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
//        CriteriaQuery<WaterMeter> criteriaQuery = cb.createQuery(WaterMeter.class);
//        Root<WaterMeter> waterMeter = criteriaQuery.from(WaterMeter.class);
//        Predicate predicate = cb.equal(waterMeter.get("apartment").get("id"), apartmentId);
//        Predicate predicateActiveWaterMeter = cb.equal(waterMeter.get("active"), true);
//        Predicate predicateExpiryDate = cb.greaterThan(waterMeter.get("expiryDate"), currentDate);
//        Predicate finalPredicate = cb.and(predicateActiveWaterMeter, predicate, predicateExpiryDate);
//        criteriaQuery.where(finalPredicate);
//
//        return getEntityManager().createQuery(criteriaQuery)
//                .getResultList();
        TypedQuery<WaterMeter> query = em.createNamedQuery(
                "WaterMeter.findAllActiveByApartmentIdAndDate", WaterMeter.class);
        query.setParameter("apartmentId", apartmentId);
        query.setParameter("currentDate", currentDate);
        return query.getResultList();
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<Apartment> findApartmentsByWaterMeterIds(final List<Long> waterMeterIds) {
        return em.createNamedQuery("WaterMeter.findApartmentsByWaterMeters", Apartment.class)
                .setFlushMode(FlushModeType.COMMIT)
                .setParameter("meterIds", waterMeterIds)
                .getResultList();
    }
}
