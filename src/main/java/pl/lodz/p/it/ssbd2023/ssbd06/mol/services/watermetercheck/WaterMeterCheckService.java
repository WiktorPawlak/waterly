package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterCheckFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@RequestScoped
public class WaterMeterCheckService {

    @Inject
    private WaterMeterCheckFacade waterMeterCheckFacade;

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<WaterMeterCheck> findWaterMeterCheckForCheckDate(final LocalDate checkDate, final WaterMeter waterMeter) {
        return waterMeterCheckFacade.findWaterMeterCheckByDateAndWaterMeterId(checkDate, waterMeter.getId());
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public void addWaterMeterCheck(final WaterMeterCheck check) {
        waterMeterCheckFacade.create(check);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public WaterMeterCheck updateWaterMeterCheck(final WaterMeterCheck waterMeterCheck) {
        return waterMeterCheckFacade.update(waterMeterCheck);
    }
}
