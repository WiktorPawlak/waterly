package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@RequestScoped
public class TariffFacade extends AbstractFacade<Tariff> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public TariffFacade() {
        super(Tariff.class);
    }

    @Override
    @PermitAll
    public List<Tariff> findAll() {
        return super.findAll();
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER})
    public Tariff create(final Tariff entity) {
        var tariff = super.create(entity);
        getEntityManager().lock(entity.getEntityConsistenceAssurance(), LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        return tariff;
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER})
    public Tariff update(final Tariff entity) {
        return super.update(entity);
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER})
    public Tariff findById(final Long id) {
        return super.findById(id);
    }

    @PermitAll
    public List<Tariff> findTariffs(final int page, final int pageSize, final boolean ascOrder, final String orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tariff> query = cb.createQuery(Tariff.class);
        Root<Tariff> tariff = query.from(Tariff.class);
        if (ascOrder) {
            query.orderBy(cb.asc(tariff.get(orderBy)));
        } else {
            query.orderBy(cb.desc(tariff.get(orderBy)));
        }
        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    @PermitAll
    public Optional<Tariff> findTariffForYearMonth(final LocalDate date) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tariff> query = cb.createQuery(Tariff.class);
            Root<Tariff> tariffRoot = query.from(Tariff.class);

            Predicate startDatePredicate = cb.lessThanOrEqualTo(tariffRoot.get("startDate"), date);
            Predicate endDatePredicate = cb.greaterThanOrEqualTo(tariffRoot.get("endDate"), date);
            Predicate datePredicate = cb.and(startDatePredicate, endDatePredicate);

            query.select(tariffRoot).where(datePredicate);

            return Optional.of(em.createQuery(query).getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @PermitAll
    public Long count() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Tariff> tariff = query.from(Tariff.class);
        query.select(cb.count(tariff));
        return em.createQuery(query).getSingleResult();
    }
}
