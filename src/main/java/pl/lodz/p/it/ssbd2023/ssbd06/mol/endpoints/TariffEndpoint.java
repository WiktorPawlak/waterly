package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService.FIRST_PAGE;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.io.Serializable;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateTariffDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.GetPagedTariffsListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.TariffsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.TariffService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.UpdateBillsService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@RequestScoped
public class TariffEndpoint implements Serializable {

    @Inject
    private TariffService tariffService;

    @ConfigProperty(name = "default.list.page.size")
    private int defaultListPageSize;

    @Inject
    private UpdateBillsService updateBillsService;

    @RolesAllowed({FACILITY_MANAGER})
    public void addTariff(final CreateTariffDto createTariffDto) {
        tariffService.addTariff(createTariffDto);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateTariff(final long id, final TariffsDto dto) {
        Tariff tariff = tariffService.findById(id);
        if (dto.getVersion() != tariff.getVersion()) {
            throw ApplicationBaseException.optimisticLockException();
        }
        tariffService.updateTariff(tariff, dto);
        updateBillsService.updateBillsOnTariffUpdate();
    }

    @PermitAll
    public PaginatedList<TariffsDto> getTariffsList(final GetPagedTariffsListDto dto) {
        int pageResolved = dto.getPage() != null ? dto.getPage() : FIRST_PAGE;
        int pageSizeResolved = dto.getPageSize() != null ? dto.getPageSize() : defaultListPageSize;
        String orderByResolved = dto.getOrderBy() != null ? dto.getOrderBy() : "startDate";
        List<TariffsDto> tariffs = tariffService.getTariffs(pageResolved,
                        pageSizeResolved,
                        dto.getOrder(),
                        orderByResolved).stream()
                .map(TariffsDto::new)
                .toList();

        return new PaginatedList<>(tariffs,
                pageResolved,
                tariffs.size(),
                (long) Math.ceil(tariffService.getTariffsCount().doubleValue() / pageSizeResolved));
    }

    @RolesAllowed({FACILITY_MANAGER})
    public TariffsDto findById(final long id) {
        return new TariffsDto(tariffService.findById(id));
    }
}
