package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.MAIN;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotSupportedException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterCheckFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterFacade;
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

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void addWaterMeterCheck() {
        waterMeterCheckFacade.create(null);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void changeActiveStatus(final long id, final boolean active) {
        //get WaterMeter; set activeStatus; flush
        throw new NotSupportedException();
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
        waterMeterFacade.create(new WaterMeter());
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void createMainWaterMeter(final CreateMainWaterMeterDto dto) {
        // check if main waterMeter already set
        waterMeterFacade.findAllByType(MAIN);
        // map dto to entity
        waterMeterFacade.create(new WaterMeter());
    }
}
