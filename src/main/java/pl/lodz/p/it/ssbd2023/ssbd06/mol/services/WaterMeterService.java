package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.MAIN;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.EntityConsistenceAssurance;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@Monitored
@ServiceExceptionHandler
@RequestScoped
public class WaterMeterService {

    @Inject
    private WaterMeterFacade waterMeterFacade;

    @Inject
    private TimeProvider timeProvider;

    @RolesAllowed(FACILITY_MANAGER)
    public WaterMeter findById(final long id) {
        return waterMeterFacade.findById(id);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Optional<WaterMeter> findActiveMainWaterMeter() {
        return waterMeterFacade.findAllActiveByType(MAIN).stream()
                .findFirst();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Optional<WaterMeter> findActiveBySerialNumber(final String serialNumber) {
        return waterMeterFacade.findAllActiveBySerialNumber(serialNumber).stream()
                .findFirst();
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterMeter findWaterMeterById(final long id) {
        return waterMeterFacade.findById(id);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void changeActiveStatus(final WaterMeter waterMeter, final boolean active) {
        waterMeter.setActive(active);
        waterMeterFacade.update(waterMeter);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void updateWaterMeter(final WaterMeter waterMeter) {
        waterMeterFacade.update(waterMeter);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void addWaterMeter(final WaterMeter waterMeter) {
        waterMeterFacade.createWaterMeter(waterMeter);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void assignWaterMeter(
            final Apartment apartment,
            final AssignWaterMeterDto dto,
            final BigDecimal expectedDailyUsage,
            final EntityConsistenceAssurance entityConsistenceAssurance
    ) {
        WaterMeter waterMeter = new WaterMeter(dto, apartment, expectedDailyUsage, entityConsistenceAssurance);
        waterMeterFacade.createWaterMeter(waterMeter);
        List<WaterMeter> waterMeters = apartment.getWaterMeters();
        waterMeters.add(waterMeter);
        apartment.setWaterMeters(waterMeters);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void createMainWaterMeter(final WaterMeter waterMeter) {
        waterMeterFacade.createWaterMeter(waterMeter);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<WaterMeter> getWaterMetersByApartmentId(final long apartmentId) {
        return waterMeterFacade.findAllByApartmentId(apartmentId, timeProvider.currentDate());
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Tuple2<List<WaterMeter>, Long> getWaterMeters(final String pattern,
                                                         final int page,
                                                         final int pageSize,
                                                         final boolean ascOrder,
                                                         final String orderBy) {

        return Tuple.of(
                waterMeterFacade.findWaterMeters(pattern, page, pageSize, ascOrder, orderBy),
                waterMeterFacade.countAll(pattern)
        );
    }
}
