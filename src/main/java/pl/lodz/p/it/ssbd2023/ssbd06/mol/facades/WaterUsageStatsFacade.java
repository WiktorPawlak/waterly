package pl.lodz.p.it.ssbd2023.ssbd06.mol.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.YearMonth;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@FacadeExceptionHandler
@RequestScoped
public class WaterUsageStatsFacade extends AbstractFacade<WaterUsageStats> {

    @PersistenceContext(unitName = "molPU")
    private EntityManager em;

    public WaterUsageStatsFacade() {
        super(WaterUsageStats.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterUsageStats update(final WaterUsageStats entity) {
        return super.update(entity);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<WaterUsageStats> findByApartmentIdAndYearMonth(final long apartmentId, final YearMonth yearMonth) {
        try {
            return Optional.of(em.createNamedQuery("WaterUsageStats.findByApartmentIdAndYearMonth", WaterUsageStats.class)
                    .setParameter("apartmentId", apartmentId)
                    .setParameter("yearMonth", yearMonth)
                    .getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterUsageStats create(final WaterUsageStats entity) {
        return super.create(entity);
    }
}
