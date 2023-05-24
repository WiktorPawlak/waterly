package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateTariffDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.TariffsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateTariffDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.TariffService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TariffEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    TariffService tariffService;

    @PermitAll
    public List<TariffsDto> getAllTariffs() {
        return tariffService.getAllTariffs().stream()
                .map(TariffsDto::new)
                .collect(Collectors.toList());
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void addTariff(final CreateTariffDto createTariffDto) {
        // dodanie taryfy
        tariffService.addTariff(new Tariff());
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateTariff(final long id, final UpdateTariffDto updateTariffDto) {
        tariffService.updateTariff(new Tariff());
    }

}
