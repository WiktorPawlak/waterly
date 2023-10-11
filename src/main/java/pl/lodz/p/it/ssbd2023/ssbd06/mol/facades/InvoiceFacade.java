package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
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
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@RequestScoped
public class InvoiceFacade extends AbstractFacade<Invoice> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public InvoiceFacade() {
        super(Invoice.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @RolesAllowed({FACILITY_MANAGER})
    @Override
    public List<Invoice> findAll() {
        return super.findAll();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Invoice> findInvoices(final String pattern,
                                      final int page,
                                      final int pageSize,
                                      final boolean ascOrder,
                                      final String orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Invoice> query = cb.createQuery(Invoice.class);
        Root<Invoice> invoiceRoot = query.from(Invoice.class);

        if (ascOrder) {
            query.orderBy(cb.asc(resolveFieldClass(invoiceRoot, orderBy).get(orderBy)));
        } else {
            query.orderBy(cb.desc(resolveFieldClass(invoiceRoot, orderBy).get(orderBy)));
        }

        Predicate predicate = cb.conjunction();

        if (pattern != null) {
            predicate = cb.and(predicate, cb.or(getFilterByPatternPredicates(pattern, cb, invoiceRoot)));
        }

        query.where(predicate);

        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    private From<?, ?> resolveFieldClass(final Root<Invoice> root, final String fieldName) {
        return switch (fieldName) {
            case "waterUsage", "totalCost", "date" -> root;
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
        Root<Invoice> invoice = query.from(Invoice.class);

        Predicate predicate = cb.conjunction();

        if (pattern != null) {
            predicate = cb.and(predicate, cb.or(getFilterByPatternPredicates(pattern, cb, invoice)));
        }

        query.where(predicate);
        query.select(cb.count(invoice));

        return em.createQuery(query).getSingleResult();
    }

    private Predicate[] getFilterByPatternPredicates(final String pattern, final CriteriaBuilder cb, final Root<Invoice> invoice) {
        String filterPattern = "%" + pattern.toUpperCase() + "%";

        Predicate numberPredicate = cb.like(cb.upper(invoice.get("invoiceNumber")), filterPattern);

        return new Predicate[]{numberPredicate};
    }

    @Override
    @RolesAllowed(FACILITY_MANAGER)
    public Invoice create(final Invoice entity) {
        return super.create(entity);
    }

    @RolesAllowed({FACILITY_MANAGER})
    @Override
    public Invoice update(final Invoice invoice) {
        return super.update(invoice);
    }

    @RolesAllowed({FACILITY_MANAGER})
    @Override
    public Invoice findById(final Long id) {
        return super.findById(id);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<Invoice> findInvoiceForYearMonth(final LocalDate date) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Invoice> cq = cb.createQuery(Invoice.class);
            Root<Invoice> root = cq.from(Invoice.class);

            YearMonth yearMonth = YearMonth.from(date);
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();

            Date startDateAsDate = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDateAsDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Predicate predicate = cb.between(root.get("date"), startDateAsDate, endDateAsDate);
            cq.where(predicate);

            return Optional.of(em.createQuery(cq).getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Optional<BigDecimal> findAverageWaterUsage() {
        TypedQuery<Double> query = em.createNamedQuery("Invoice.findAverageWaterUsage", Double.class);
        try {
            return Optional.of(BigDecimal.valueOf(query.getSingleResult()));
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }
}
