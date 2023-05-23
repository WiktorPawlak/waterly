package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotSupportedException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterCheckFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterFacade;
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
    public void disableWaterMeter(final long id) {
        //get WaterMeter; set inactive; flush
        throw new NotSupportedException();
    }
}
