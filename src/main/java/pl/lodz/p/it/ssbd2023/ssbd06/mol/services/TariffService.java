package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateTariffDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.TariffsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.TariffFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@RequestScoped
public class TariffService {

    @Inject
    TariffFacade tariffFacade;

    @Inject
    EntityConsistenceAssuranceService entityConsistenceAssuranceService;


    @PermitAll
    public List<Tariff> getAllTariffs() {
        return tariffFacade.findAll();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void addTariff(final CreateTariffDto createTariffDto) {
        YearMonth startYearMonth = YearMonth.from(DateConverter.convertStringDateToLocalDate(createTariffDto.getStartDate()));
        YearMonth endYearMonth = YearMonth.from(DateConverter.convertStringDateToLocalDate(createTariffDto.getEndDate()));

        Tariff tariff = Tariff.builder()
                .coldWaterPrice(createTariffDto.getColdWaterPrice())
                .hotWaterPrice(createTariffDto.getHotWaterPrice())
                .trashPrice(createTariffDto.getTrashPrice())
                .startDate(startYearMonth.atDay(1))
                .endDate(endYearMonth.atEndOfMonth())
                .entityConsistenceAssurance(entityConsistenceAssuranceService.findTariffConsistencyAssurance().orElse(null))
                .build();

        boolean tariffsCollidingFlag = !findTariffsContainingPeriod(tariff.getStartDate(), tariff.getEndDate())
                .stream().toList().isEmpty();
        if (tariffsCollidingFlag) {
            throw ApplicationBaseException.tariffsColidingException();
        }
        if (tariff.getEndDate().isBefore(tariff.getStartDate()) ||
                tariff.getStartDate().equals(tariff.getEndDate())) {
            throw ApplicationBaseException.invalidTariffPeriodException();
        }

        tariffFacade.create(tariff);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateTariff(final Tariff tariff, final TariffsDto updatedObject) {
        tariff.setColdWaterPrice(updatedObject.getColdWaterPrice());
        tariff.setHotWaterPrice(updatedObject.getHotWaterPrice());
        tariff.setTrashPrice(updatedObject.getTrashPrice());

        if (updatedObject.getEndDate().isBefore(updatedObject.getStartDate()) ||
                updatedObject.getStartDate().equals(updatedObject.getEndDate())) {
            throw ApplicationBaseException.invalidTariffPeriodException();
        }

        checkAndSetTariffPeriod(tariff, updatedObject);

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

    @RolesAllowed({FACILITY_MANAGER})
    public Tariff findById(final long id) {
        return tariffFacade.findById(id);
    }

    private List<Tariff> findTariffsContainingPeriod(final LocalDate startDate, final LocalDate endDate) {
        return tariffFacade.findAll().stream()
                .filter(tariff -> isOverlapping(tariff, startDate, endDate))
                .toList();
    }


    private static boolean isOverlapping(final Tariff tariff, final LocalDate startDate, final LocalDate endDate) {
        YearMonth tariffStartYearMonth = YearMonth.from(tariff.getStartDate());
        YearMonth tariffEndYearMonth = YearMonth.from(tariff.getEndDate());
        LocalDate tariffPeriodStart = tariffStartYearMonth.atDay(1);
        LocalDate tariffPeriodEnd = tariffEndYearMonth.atEndOfMonth();

        return tariffPeriodStart.isBefore(endDate) && tariffPeriodEnd.isAfter(startDate);
    }

    private void checkAndSetTariffPeriod(final Tariff tariff, final TariffsDto updatedObject) {
        boolean tariffsColidingFlag = findTariffsContainingPeriod(updatedObject.getStartDate(), updatedObject.getEndDate())
                .stream()
                .anyMatch(foundTariff -> foundTariff.getId() != tariff.getId());
        if (tariffsColidingFlag) {
            throw ApplicationBaseException.tariffsColidingException();
        } else {
            YearMonth startYearMonth = YearMonth.from(updatedObject.getStartDate());
            YearMonth endYearMonth = YearMonth.from(updatedObject.getEndDate());
            tariff.setStartDate(startYearMonth.atDay(1));
            tariff.setEndDate(endYearMonth.atEndOfMonth());
        }
    }
}
