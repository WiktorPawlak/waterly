package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDate;
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
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class BillFacade extends AbstractFacade<Bill> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public BillFacade() {
        super(Bill.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    public Bill create(final Bill bill) {
        return super.create(bill);
    }

    public Bill update(final Bill bill) {
        return super.update(bill);
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER})
    public Optional<Bill> findByDateAndApartmentId(final LocalDate date, final long apartmentId) {
        try {
            TypedQuery<Bill> billsByOwnerIdTypedQuery = em.createNamedQuery("Bill.findBillsByYearAndMonthAndApartmentId", Bill.class);
            billsByOwnerIdTypedQuery.setFlushMode(FlushModeType.COMMIT);
            billsByOwnerIdTypedQuery.setParameter("apartmentId", apartmentId);
            billsByOwnerIdTypedQuery.setParameter("month", date.getMonthValue());
            billsByOwnerIdTypedQuery.setParameter("year", date.getYear());
            return Optional.of(billsByOwnerIdTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }


    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<Bill> findByApartmentId(final long apartmentId) {
        TypedQuery<Bill> billsByApartmentIdTypedQuery = em.createNamedQuery("Bill.findBillsByApartmentId", Bill.class);
        billsByApartmentIdTypedQuery.setFlushMode(FlushModeType.COMMIT);
        billsByApartmentIdTypedQuery.setParameter("apartmentId", apartmentId);
        return billsByApartmentIdTypedQuery.getResultList();
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Bill findById(final Long id) {
        return super.findById(id);
    }

    public Optional<Bill> findBillById(final Long id) {
        return Optional.ofNullable(getEntityManager().find(Bill.class, id));
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<Long> findBillOwnerIdByApartmentIdAndDate(final long apartmentId, final LocalDate billDate) {
        try {
            TypedQuery<Long> typedQuery = em.createNamedQuery("Bill.findBillOwnerIdByApartmentIdAndDate", Long.class);
            typedQuery.setFlushMode(FlushModeType.COMMIT);
            typedQuery.setParameter("apartmentId", apartmentId);
            typedQuery.setParameter("billDate", billDate);
            return Optional.of(typedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Bill> findBillsWithNullRealUsage() {
        TypedQuery<Bill> typedQuery = em.createNamedQuery("Bill.findBillsWithNullRealUsage", Bill.class);
        typedQuery.setFlushMode(FlushModeType.COMMIT);
        return typedQuery.getResultList();
    }

    public List<Bill> findBillsByDate(final LocalDate date) {
        TypedQuery<Bill> typedQuery = em.createNamedQuery("Bill.findBillsByDate", Bill.class);
        typedQuery.setParameter("month", date.getMonthValue());
        typedQuery.setParameter("year", date.getYear());
        typedQuery.setFlushMode(FlushModeType.COMMIT);
        return typedQuery.getResultList();
    }
}
