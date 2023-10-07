package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
@RequestScoped
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
        return findApartments(null, pattern, page, pageSize, ascOrder, orderBy);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Long countAll(final String pattern) {
        return count(null, pattern);
    }

    @RolesAllowed(OWNER)
    public List<Apartment> findOwnerAllApartments(final Long ownerId,
                                                  final String pattern,
                                                  final int page,
                                                  final int pageSize,
                                                  final boolean ascOrder,
                                                  final String orderBy) {
        return findApartments(ownerId, pattern, page, pageSize, ascOrder, orderBy);
    }

    @RolesAllowed(OWNER)
    public Long countAllOwnerApartments(final Long ownerId, final String pattern) {
        return count(ownerId, pattern);
    }

    public Apartment findByWaterMeterId(final long waterMeterId) {
        throw new NotSupportedException();
    }

    private List<Apartment> findApartments(final Long ownerId,
                                           final String pattern,
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

        Predicate predicate = cb.conjunction();

        if (pattern != null) {
            predicate = cb.and(predicate, cb.or(getFilterByPatternPredicates(pattern, cb, apartmentRoot)));
        }

        if (ownerId != null) {
            predicate = cb.and(predicate, cb.equal(apartmentRoot.get("owner"), ownerId));
        }

        query.where(predicate);

        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    private Long count(final Long ownerId, final String pattern) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Apartment> apartment = query.from(Apartment.class);

        Predicate predicate = cb.conjunction();

        if (pattern != null) {
            predicate = cb.and(predicate, cb.or(getFilterByPatternPredicates(pattern, cb, apartment)));
        }

        if (ownerId != null) {
            predicate = cb.and(predicate, cb.equal(apartment.get("owner"), ownerId));
        }

        query.where(predicate);
        query.select(cb.count(apartment));

        return em.createQuery(query).getSingleResult();
    }

    private Predicate[] getFilterByPatternPredicates(final String pattern, final CriteriaBuilder cb, final Root<Apartment> apartment) {
        String filterPattern = "%" + pattern.toUpperCase() + "%";

        Predicate numberPredicate = cb.like(cb.upper(apartment.get("number")), filterPattern);

        return new Predicate[]{numberPredicate};
    }

    private From<?, ?> resolveFieldClass(final Root<Apartment> root, final String fieldName) {
        return switch (fieldName) {
            case "number", "area", "id" -> root;
            default -> {
                log.severe(() -> "Error, trying to query by invalid field");
                throw ApplicationBaseException.generalErrorException();
            }
        };
    }
}
