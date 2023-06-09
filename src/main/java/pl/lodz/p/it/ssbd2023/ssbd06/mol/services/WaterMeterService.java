package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.MAIN;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterCheckFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class WaterMeterService {

    @Inject
    private WaterMeterFacade waterMeterFacade;
    @Inject
    private WaterMeterCheckFacade waterMeterCheckFacade;


    @RolesAllowed(FACILITY_MANAGER)
    public Optional<WaterMeter> findActiveMainWaterMeter() {
        return waterMeterFacade.findAllActiveByType(MAIN).stream()
                .findFirst();
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void addWaterMeterCheck() {
        waterMeterCheckFacade.create(null);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void changeActiveStatus(final long id, final boolean active) {
        var waterMeter = waterMeterFacade.findById(id);
        waterMeter.setActive(active);
        waterMeterFacade.update(waterMeter);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void updateWaterMeter(final long id, final UpdateWaterMeterDto dto) {
        waterMeterFacade.findById(id);
        // map new fields to old entity
        waterMeterFacade.update(new WaterMeter());
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void addReplacementWaterMeter(final long id, final ReplaceWaterMeterDto dto) {
        waterMeterFacade.findById(id);
        // merge dto and old entity (apartment)
        // ReplaceWaterMeterDto -> WaterMeter
        waterMeterFacade.create(new WaterMeter());
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void assignWaterMeter(final Apartment apartment, final AssignWaterMeterDto dto) {
        //apartment + dto -> WaterMeter
        waterMeterFacade.create(new WaterMeter());
    }

    @SneakyThrows
    @RolesAllowed(FACILITY_MANAGER)
    public void createMainWaterMeter(final WaterMeter waterMeter){
        waterMeterFacade.create(waterMeter);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<WaterMeter> getWaterMetersByApartmentId(final long apartmentId) {
        return waterMeterFacade.findAllByApartmentId(apartmentId);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<WaterMeter> getWaterMeters(final int page, final int pageSize, final String order, final String orderBy) {
        boolean ascOrder = "asc".equalsIgnoreCase(order);
        return waterMeterFacade.findWaterMeters(page,
                pageSize,
                ascOrder,
                orderBy);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Long getWaterMetersCount() {
        return waterMeterFacade.count();
    }
}
