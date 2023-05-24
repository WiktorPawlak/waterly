package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterMeterService;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WaterMeterEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private WaterMeterService waterMeterService;

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void performWaterMeterCheck(final WaterMeterCheckDto dto) {
        waterMeterService.addWaterMeterCheck();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void changeWaterMeterActiveStatus(final long id, final WaterMeterActiveStatusDto dto) {
        waterMeterService.changeActiveStatus(id, dto.isActive());
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void updateWaterMeter(final long id, final UpdateWaterMeterDto dto) {
        waterMeterService.updateWaterMeter(id, dto);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void replaceWaterMeter(final long id, final ReplaceWaterMeterDto dto) {
        waterMeterService.changeActiveStatus(id, false);
        waterMeterService.addReplacementWaterMeter(id, dto);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void createMainWaterMeter(final CreateMainWaterMeterDto dto) {
        waterMeterService.createMainWaterMeter(dto);
    }
}
