package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.MAIN;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.vavr.Tuple2;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.config.PaginationConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateMainWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ReplaceWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.UpdateWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.ApartmentService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterMeterService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingBean;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@Log
@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class WaterMeterEndpoint extends TransactionBoundariesTracingBean {

    @Inject
    private WaterMeterService waterMeterService;
    @Inject
    private ApartmentService apartmentService;
    @Inject
    private TimeProvider timeProvider;

    @Inject
    private PaginationConfig paginationConfig;

    @RolesAllowed({FACILITY_MANAGER})
    public WaterMeterDto getWaterMeterById(final long id) {
        return new WaterMeterDto(waterMeterService.findById(id));
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void changeWaterMeterActiveStatus(final long id, final WaterMeterActiveStatusDto dto) {
        WaterMeter waterMeter = waterMeterService.findWaterMeterById(id);
        if (Objects.equals(waterMeter.getType(), MAIN)) {
            checkIfMainWaterMeterActive(waterMeter.getId());
        }
        waterMeterService.changeActiveStatus(waterMeter, dto.isActive());
    }

    private void checkIfMainWaterMeterActive(final long id) {
        Optional<WaterMeter> activeMainWaterMeter = waterMeterService.findActiveMainWaterMeter();
        if (activeMainWaterMeter.isPresent() && activeMainWaterMeter.get().getId() != id) {
            throw ApplicationBaseException.mainWaterMeterAlreadyExistsException();
        }
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void updateWaterMeter(final long id, final UpdateWaterMeterDto dto) {
        validateExpiryDate(dto.getExpiryDate());
        WaterMeter waterMeter = waterMeterService.findById(id);
        if (waterMeter.getVersion() != dto.getVersion()) {
            throw ApplicationBaseException.optimisticLockException();
        }
        updateWaterMeterEntity(waterMeter, dto);
        waterMeterService.updateWaterMeter(waterMeter);
    }

    private void updateWaterMeterEntity(final WaterMeter waterMeter, final UpdateWaterMeterDto dto) {
        waterMeter.setSerialNumber(dto.getSerialNumber());
        waterMeter.setExpiryDate(DateConverter.convert(dto.getExpiryDate()));
        waterMeter.setStartingValue(
                dto.getStartingValue() == null ? waterMeter.getStartingValue() : dto.getStartingValue()
        );

        switch (waterMeter.getType()) {
            case COLD_WATER, HOT_WATER -> {
                waterMeter.setExpectedDailyUsage(
                        dto.getExpectedDailyUsage() == null || dto.getExpectedDailyUsage().isBlank() ?
                                null : new BigDecimal(dto.getExpectedDailyUsage())
                );
                if (dto.getApartmentId() != null && dto.getApartmentId() != waterMeter.getApartment().getId()) {
                    if (!waterMeter.getWaterMeterChecks().isEmpty()) {
                        throw ApplicationBaseException.waterMeterHasWaterMeterChecksException();
                    }
                    Apartment apartment = apartmentService.getApartmentById(dto.getApartmentId());
                    waterMeter.setApartment(apartment);
                }
            }
            default -> {}
        }
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void replaceWaterMeter(final long waterMeterId, final ReplaceWaterMeterDto dto) {
        WaterMeter waterMeter = waterMeterService.findWaterMeterById(waterMeterId);
        if (!waterMeter.isActive()) {
            throw ApplicationBaseException.inactiveWaterMeterException();
        }
        waterMeterService.changeActiveStatus(waterMeter, false);
        WaterMeter newWaterMeter = prepareNewWaterMeter(waterMeter, dto);
        waterMeterService.addWaterMeter(newWaterMeter);
    }

    private WaterMeter prepareNewWaterMeter(final WaterMeter oldWaterMeter, final ReplaceWaterMeterDto dto) {
        return WaterMeter.builder()
                .serialNumber(dto.getSerialNumber())
                .expiryDate(DateConverter.convert(dto.getExpiryDate()))
                .startingValue(dto.getStartingValue())
                .type(oldWaterMeter.getType())
                .apartment(oldWaterMeter.getApartment())
                .expectedDailyUsage(oldWaterMeter.getExpectedDailyUsage())
                .active(true)
                .build();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void addWaterMeter(final long apartmentId, final AssignWaterMeterDto dto) {
        if (DateConverter.convert(dto.getExpiryDate()).before(timeProvider.currentDate())) {
            throw ApplicationBaseException.expiryDateAlreadyExpiredException();
        }
        waterMeterService.assignWaterMeter(apartmentService.getApartmentById(apartmentId), dto);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void createMainWaterMeter(final CreateMainWaterMeterDto dto) {
        validateExpiryDate(dto.getExpiryDate());
        Optional<WaterMeter> mainWaterMeter = waterMeterService.findActiveMainWaterMeter();
        if (mainWaterMeter.isPresent()) {
            throw ApplicationBaseException.mainWaterMeterAlreadyExistsException();
        }
        waterMeterService.createMainWaterMeter(prepareMainWaterMeter(dto));
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<WaterMeterDto> getWaterMetersByApartmentId(final long apartmentId) {
        List<WaterMeter> waterMeters = waterMeterService.getWaterMetersByApartmentId(apartmentId);
        return waterMeters.stream().map(WaterMeterDto::new).toList();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public PaginatedList<WaterMeterDto> getWaterMetersList(final String pattern,
                                                      final Integer page,
                                                      final Integer pageSize,
                                                      final String order,
                                                      final String orderBy) {

        int preparedPage = paginationConfig.preparePage(page);
        int preparedPageSize = paginationConfig.preparePageSize(pageSize);
        String preparedOrderBy = orderBy != null ? orderBy : "serialNumber";
        String preparedPattern = paginationConfig.preparePattern(pattern);
        boolean ascOrder = paginationConfig.prepareAscOrder(order);

        Tuple2<List<WaterMeter>, Long> paginatedWaterMeters =
                waterMeterService.getWaterMeters(preparedPattern, preparedPage, preparedPageSize, ascOrder, preparedOrderBy);

        List<WaterMeterDto> waterMetersDtos = paginatedWaterMeters._1
                .stream().map(WaterMeterDto::new)
                .toList();
        return new PaginatedList<>(
                waterMetersDtos,
                preparedPage,
                waterMetersDtos.size(),
                (long) Math.ceil(paginatedWaterMeters._2.doubleValue() / preparedPageSize)
        );
    }

    private WaterMeter prepareMainWaterMeter(final CreateMainWaterMeterDto dto) {
        return WaterMeter.builder()
                .active(true)
                .type(MAIN)
                .serialNumber(dto.getSerialNumber())
                .expectedDailyUsage(BigDecimal.ZERO)
                .startingValue(dto.getStartingValue())
                .expiryDate(DateConverter.convert(dto.getExpiryDate()))
                .build();
    }

    private void validateExpiryDate(final String expiryDate) {
        if (DateConverter.convert(expiryDate).before(timeProvider.currentDate())) {
            throw ApplicationBaseException.expiryDateAlreadyExpiredException();
        }
    }

}
