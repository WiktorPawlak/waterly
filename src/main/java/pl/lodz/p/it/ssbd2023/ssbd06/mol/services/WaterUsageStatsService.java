package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.YearMonth;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterUsageStatsFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WaterUsageStatsService {

    @Inject
    private WaterUsageStatsFacade waterUsageStatsFacade;

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterUsageStats createWaterUsageStats(final WaterUsageStats waterUsageStats) {
        return waterUsageStatsFacade.create(waterUsageStats);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void updateWaterUsageStats(final WaterUsageStats waterUsageStats) {
        waterUsageStatsFacade.update(waterUsageStats);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<WaterUsageStats> findByApartmentAndYearMonth(final Apartment apartment, final YearMonth yearMonth) {
        return waterUsageStatsFacade.findByApartmentIdAndYearMonth(apartment.getId(), yearMonth);
    }
}
