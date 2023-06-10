package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
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
    public List<WaterMeter> findWaterMeters(final int page, final int pageSize, final boolean ascOrder, final String orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<WaterMeter> query = cb.createQuery(WaterMeter.class);
        Root<WaterMeter> waterMeter = query.from(WaterMeter.class);
        if (ascOrder) {
            query.orderBy(cb.asc(waterMeter.get(orderBy)));
        } else {
            query.orderBy(cb.desc(waterMeter.get(orderBy)));
        }
        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Long count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<WaterMeter> waterMeter = query.from(WaterMeter.class);
        query.select(cb.count(waterMeter));
        return em.createQuery(query).getSingleResult();
    }

    public List<WaterMeter> findAllByApartmentId(final long apartmentId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<WaterMeter> criteriaQuery = cb.createQuery(WaterMeter.class);
        Root<WaterMeter> waterMeter = criteriaQuery.from(WaterMeter.class);
        Predicate predicate = cb.equal(waterMeter.get("apartment").get("id"), apartmentId);
        criteriaQuery.where(predicate);

        return getEntityManager().createQuery(criteriaQuery)
                .getResultList();
    }
}
