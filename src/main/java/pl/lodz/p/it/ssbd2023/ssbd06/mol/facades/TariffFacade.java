package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
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
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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
        return super.create(entity);
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER})
    public Tariff update(final Tariff entity) {
        return super.update(entity);
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
    public Tariff findTariffForDate(final LocalDate date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tariff> cq = cb.createQuery(Tariff.class);
        Root<Tariff> root = cq.from(Tariff.class);
        YearMonth yearMonth = YearMonth.from(date);
        LocalDate startDate = yearMonth.atDay(1); // First day of the specified month
        LocalDate endDate = yearMonth.atEndOfMonth(); // Last day of the specified month

        Date startDateAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Predicate predicate = cb.between(root.get("startDate"), startDateAsDate, endDateAsDate);

        cq.where(predicate);

        return em.createQuery(cq).getSingleResult();
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
