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
import lombok.extern.java.Log;
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

    @RolesAllowed({FACILITY_MANAGER})
    @Override
    public Apartment create(final Apartment entity) {
        return super.create(entity);
    }

    @RolesAllowed({FACILITY_MANAGER})
    @Override
    public Apartment update(final Apartment entity) {
        return super.update(entity);
    }

    @RolesAllowed({FACILITY_MANAGER})
    @Override
    public Apartment findById(final Long id) {
        return super.findById(id);
    }

    @RolesAllowed({FACILITY_MANAGER})
    @Override
    public List<Apartment> findAll() {
        return super.findAll();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Apartment> findOwnerAllApartments(final long ownerId) {
        TypedQuery<Apartment> billsByApartmentIdTypedQuery = em.createNamedQuery("Apartment.findByOwner_Id", Apartment.class);
        billsByApartmentIdTypedQuery.setFlushMode(FlushModeType.COMMIT);
        billsByApartmentIdTypedQuery.setParameter("ownerId", ownerId);
        return billsByApartmentIdTypedQuery.getResultList();
    }
}
