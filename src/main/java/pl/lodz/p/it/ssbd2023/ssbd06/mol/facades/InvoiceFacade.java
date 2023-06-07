package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

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
import jakarta.persistence.criteria.Root;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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
    public List<Invoice> findInvoices(final int page, final int pageSize, final boolean ascOrder, final String orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Invoice> query = cb.createQuery(Invoice.class);
        Root<Invoice> invoice = query.from(Invoice.class);
        if (ascOrder) {
            query.orderBy(cb.asc(invoice.get(orderBy)));
        } else {
            query.orderBy(cb.desc(invoice.get(orderBy)));
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
        Root<Invoice> invoice = query.from(Invoice.class);
        query.select(cb.count(invoice));
        return em.createQuery(query).getSingleResult();
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

}
