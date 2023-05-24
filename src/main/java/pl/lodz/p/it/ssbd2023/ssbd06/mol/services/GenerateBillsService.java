package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ApartmentFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.BillFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.TariffFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class GenerateBillsService {

    @Inject
    private BillFacade billFacade;

    @Inject
    private ApartmentFacade apartmentFacade;

    @Inject
    private TariffFacade tariffFacade;

    @Inject
    private WaterMeterFacade waterMeterFacade;

    @RolesAllowed({FACILITY_MANAGER})
    public void generateBills(final Invoice invoice) {
        //fetch aparments and their water usages for last 2 months
        //calculate usages for last period
        //calculade unbilled water - subtract reading from main water meter and sum of aparments water usages from last period
        //calculate unbilled water amout per aparment based on aparment size
        //SUM IT UP AND GENERATE BILLS :D
        //todo implement
        throw new UnsupportedOperationException();
    }
}
