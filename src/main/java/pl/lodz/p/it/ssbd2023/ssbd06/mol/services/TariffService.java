package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.TariffFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TariffService {

    @Inject
    TariffFacade tariffFacade;

    @PermitAll
    public List<Tariff> getAllTariffs() {
        return tariffFacade.findAll();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void addTariff(final Tariff tariff) {
        tariffFacade.create(tariff);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateTariff(final Tariff tariff) {
        tariffFacade.update(tariff);
    }

    @PermitAll
    public List<Tariff> getTariffs(final int page, final int pageSize, final String order, final String orderBy) {
        boolean ascOrder = "asc".equalsIgnoreCase(order);
        return tariffFacade.findTariffs(page,
                pageSize,
                ascOrder,
                orderBy);
    }

    @PermitAll
    public Long getTariffsCount() {
        return tariffFacade.count();
    }
}
