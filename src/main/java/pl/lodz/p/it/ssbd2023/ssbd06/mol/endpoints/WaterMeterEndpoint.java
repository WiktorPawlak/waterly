package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService.FIRST_PAGE;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.MAIN;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.GetPagedWaterMetersListDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMetersDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.ApartmentService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterMeterService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WaterMeterEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private WaterMeterService waterMeterService;

    @Inject
    private ApartmentService apartmentService;

    @Inject
    private TimeProvider timeProvider;

    @Inject
    @Property("default.list.page.size")
    private int defaultListPageSize;

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
    public void replaceWaterMeter(final long waterMeterId, final ReplaceWaterMeterDto dto) {
        waterMeterService.changeActiveStatus(waterMeterId, false);
        waterMeterService.addReplacementWaterMeter(waterMeterId, dto);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void addWaterMeter(final long apartmentId, final AssignWaterMeterDto dto) {
        Apartment apartment = apartmentService.getApartmentById(apartmentId);
        waterMeterService.assignWaterMeter(apartment, dto);
    }

    @SneakyThrows(ParseException.class)
    @RolesAllowed(FACILITY_MANAGER)
    public void createMainWaterMeter(final CreateMainWaterMeterDto dto) {
        if (DateConverter.convert(dto.getExpiryDate()).before(timeProvider.currentDate())) {
            throw ApplicationBaseException.expiryDateAlreadyExpiredException();
        }
        Optional<WaterMeter> mainWaterMeter = waterMeterService.findActiveMainWaterMeter();
        if (mainWaterMeter.isPresent()) {
            throw ApplicationBaseException.mainWaterMeterAlreadyExistsException();
        }
        waterMeterService.createMainWaterMeter(prepareMainWaterMeter(dto));
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<WaterMeter> getWaterMetersByApartmentId(final long apartmentId) {
        return waterMeterService.getWaterMetersByApartmentId(apartmentId);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public PaginatedList<WaterMetersDto> getWaterMetersList(final @NotNull @Valid GetPagedWaterMetersListDto dto) {
        int pageResolved = dto.getPage() != null ? dto.getPage() : FIRST_PAGE;
        int pageSizeResolved = dto.getPageSize() != null ? dto.getPageSize() : defaultListPageSize;
        String orderByResolved = dto.getOrderBy() != null ? dto.getOrderBy() : "expiryDate";
        List<WaterMetersDto> waterMeters = waterMeterService.getWaterMeters(pageResolved,
                        pageSizeResolved,
                        dto.getOrder(),
                        orderByResolved).stream()
                .map(WaterMetersDto::new)
                .toList();

        return new PaginatedList<>(waterMeters,
                pageResolved,
                waterMeters.size(),
                (long) Math.ceil(waterMeterService.getWaterMetersCount().doubleValue() / pageSizeResolved));
    }

    @SneakyThrows
    private WaterMeter prepareMainWaterMeter(final CreateMainWaterMeterDto dto) {
        return WaterMeter.builder()
                .active(true)
                .type(MAIN)
                .expectedUsage(BigDecimal.ZERO)
                .startingValue(BigDecimal.ZERO)
                .expiryDate(DateConverter.convert(dto.getExpiryDate()))
                .build();
    }
}
