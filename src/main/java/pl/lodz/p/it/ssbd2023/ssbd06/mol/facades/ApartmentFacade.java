package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.ws.rs.NotSupportedException;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ApartmentFacade extends AbstractFacade<Apartment> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public ApartmentFacade() {
        super(Apartment.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @RolesAllowed(FACILITY_MANAGER)
    @Override
    public Apartment create(final Apartment entity) {
        return super.create(entity);
    }

    @RolesAllowed(FACILITY_MANAGER)
    @Override
    public Apartment update(final Apartment entity) {
        return super.update(entity);
    }

    @RolesAllowed(FACILITY_MANAGER)
    @Override
    public Apartment findById(final Long id) {
        return super.findById(id);
    }

    @RolesAllowed(FACILITY_MANAGER)
    @Override
    public List<Apartment> findAll() {
        return super.findAll();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public List<Apartment> findApartments(final String pattern,
                                          final int page,
                                          final int pageSize,
                                          final boolean ascOrder,
                                          final String orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Apartment> query = cb.createQuery(Apartment.class);
        Root<Apartment> apartmentRoot = query.from(Apartment.class);

        if (ascOrder) {
            query.orderBy(cb.asc(resolveFieldClass(apartmentRoot, orderBy).get(orderBy)));
        } else {
            query.orderBy(cb.desc(resolveFieldClass(apartmentRoot, orderBy).get(orderBy)));
        }

        if (pattern != null) {
            query.where(cb.or(getFilterByPatternPredicates(pattern, cb, apartmentRoot)));
        }

        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Long count(final String pattern) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Apartment> account = query.from(Apartment.class);

        if (pattern != null) {
            query.where(cb.or(getFilterByPatternPredicates(pattern, cb, account)));
        }

        query.select(cb.count(account));

        return em.createQuery(query).getSingleResult();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public List<Apartment> findOwnerAllApartments(final long ownerId) {
        TypedQuery<Apartment> billsByApartmentIdTypedQuery = em.createNamedQuery("Apartment.findByOwner_Id", Apartment.class);
        billsByApartmentIdTypedQuery.setFlushMode(FlushModeType.COMMIT);
        billsByApartmentIdTypedQuery.setParameter("ownerId", ownerId);
        return billsByApartmentIdTypedQuery.getResultList();
    }

    public Apartment findByWaterMeterId(final long waterMeterId) {
        throw new NotSupportedException();
    }

    private Predicate[] getFilterByPatternPredicates(final String pattern, final CriteriaBuilder cb, final Root<Apartment> apartment) {
        String filterPattern = "%" + pattern.toUpperCase() + "%";

        Predicate emailPredicate = cb.like(cb.upper(apartment.get("number")), filterPattern);

        return new Predicate[]{emailPredicate};
    }

    private From<?, ?> resolveFieldClass(final Root<Apartment> root, final String fieldName) {
        return switch (fieldName) {
            case "number", "area" -> root;
            default -> {
                log.severe(() -> "Error, trying to query by invalid field");
                throw ApplicationBaseException.generalErrorException();
            }
        };
    }
}
