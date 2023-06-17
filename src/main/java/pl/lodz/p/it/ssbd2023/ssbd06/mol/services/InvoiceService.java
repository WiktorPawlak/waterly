package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.ApartmentService.WATER_METER_SCALE;
import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService.ONE_MONTH;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.MAIN;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateInvoiceDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.InvoicesDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ApartmentFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.InvoiceFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.TariffFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterCheckFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.service.converters.DateConverter;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class InvoiceService {

    @Inject
    private InvoiceFacade invoiceFacade;

    @Inject
    private ApartmentFacade apartmentFacade;

    @Inject
    private WaterMeterCheckFacade waterMeterCheckFacade;

    @Inject
    private TariffFacade tariffFacade;

    @Inject
    private WaterMeterFacade waterMeterFacade;

    @Inject
    private TimeProvider timeProvider;

    @SneakyThrows
    @RolesAllowed({FACILITY_MANAGER})
    public void createInvoice(final CreateInvoiceDto invoice) {
        LocalDate invoiceDate = DateConverter.convertInvoiceDate(invoice.getDate());
        checkIfAllWaterMeterChecksArePresent(invoiceDate);
        checkIfTariffIsPresent(invoiceDate);
        invoiceFacade.create(new Invoice(invoice));
        createMainWaterMeterReadings(invoice, invoiceDate);
    }

    private void createMainWaterMeterReadings(final CreateInvoiceDto invoice, final LocalDate invoiceDate) {
        Optional<WaterMeter> optMainWaterMeter = waterMeterFacade.findOneActiveByType(MAIN);
        optMainWaterMeter.ifPresentOrElse(mainWaterMeter -> {
            Optional<WaterMeterCheck> mainWaterMeterCheck = waterMeterCheckFacade.findChecksForDateAndWaterMeterType(MAIN, invoiceDate);
            mainWaterMeterCheck.ifPresentOrElse(check -> {
                throw ApplicationBaseException.generalErrorException();
            }, () -> {
                Optional<WaterMeterCheck> previousMonthCheck =
                        waterMeterCheckFacade.findChecksForDateAndWaterMeterType(MAIN, invoiceDate.minusMonths(ONE_MONTH));
                previousMonthCheck.ifPresentOrElse(previousCheck -> createNewWaterMeterCheck(invoice,
                                invoiceDate,
                                mainWaterMeter,
                                previousCheck),
                        () -> createNewWaterMeterCheck(invoice,
                                invoiceDate,
                                mainWaterMeter));
            });
        }, () -> {
            throw ApplicationBaseException.generalErrorException();
        });
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateInvoice(final Invoice invoice, final InvoicesDto dto) {
        LocalDate invoiceDate = dto.getDate();
        checkIfTariffIsPresent(invoiceDate);
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setWaterUsage(dto.getWaterUsage());
        Optional<Invoice> collidingInvoice = invoiceFacade.findInvoiceForYearMonth(dto.getDate());
        collidingInvoice.ifPresentOrElse(foundInvoice -> {
            if (foundInvoice.getId() != invoice.getId()) {
                throw ApplicationBaseException.invoicesCollidingException();
            }
        }, () -> invoice.setDate(dto.getDate()));
        invoiceFacade.update(invoice);
        updateMainWaterMeterReadings(invoice, invoiceDate);
    }

    private void updateMainWaterMeterReadings(final Invoice invoice, final LocalDate invoiceDate) {
        Optional<WaterMeter> optMainWaterMeter = waterMeterFacade.findOneActiveByType(MAIN);
        optMainWaterMeter.ifPresentOrElse(mainWaterMeter -> {
            Optional<WaterMeterCheck> mainWaterMeterCheck = waterMeterCheckFacade.findChecksForDateAndWaterMeterType(MAIN, invoiceDate);
            mainWaterMeterCheck.ifPresentOrElse(check -> {
                Optional<WaterMeterCheck> previousMonthCheck =
                        waterMeterCheckFacade.findChecksForDateAndWaterMeterType(MAIN, invoiceDate.minusMonths(ONE_MONTH));
                previousMonthCheck.ifPresentOrElse(previousCheck -> updateMainWaterMeterCheck(invoice, check, previousCheck, mainWaterMeter),
                        () -> updateMainWaterMeterCheck(invoice, mainWaterMeter, check));
            }, () -> {
                throw ApplicationBaseException.generalErrorException();
            });
        }, () -> {
            throw ApplicationBaseException.generalErrorException();
        });
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Tuple2<List<Invoice>, Long> getInvoices(final String pattern,
                                                   final int page,
                                                   final int pageSize,
                                                   final boolean ascOrder,
                                                   final String orderBy) {

        return Tuple.of(
                invoiceFacade.findInvoices(pattern, page, pageSize, ascOrder, orderBy),
                invoiceFacade.countAll(pattern)
        );
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Invoice findInvoiceById(final long id) {
        return invoiceFacade.findById(id);
    }

    private void updateMainWaterMeterCheck(final Invoice invoice, final WaterMeterCheck check, final WaterMeterCheck previousCheck, WaterMeter mainWaterMeter) {
        check.setMeterReading(invoice.getWaterUsage().add(previousCheck.getMeterReading()));
        waterMeterCheckFacade.update(check);
        updateMainWaterMeterDailyUsage(mainWaterMeter);
    }

    private void updateMainWaterMeterCheck(final Invoice invoice, final WaterMeter mainWaterMeter, final WaterMeterCheck check) {
        check.setMeterReading(mainWaterMeter.getStartingValue().add(invoice.getWaterUsage()));
        waterMeterCheckFacade.update(check);
        updateMainWaterMeterDailyUsage(mainWaterMeter);
    }

    private void updateMainWaterMeterDailyUsage(final WaterMeter mainWaterMeter) {
        Optional<BigDecimal> averageUsage = invoiceFacade.findAverageWaterUsage();
        averageUsage.ifPresentOrElse(usage -> {
            int daysInCurrentMonth = timeProvider.currentLocalDate().lengthOfMonth();
            BigDecimal mainWaterMeterDailyUsage = usage.divide(BigDecimal.valueOf(daysInCurrentMonth), WATER_METER_SCALE);
            mainWaterMeter.setExpectedDailyUsage(mainWaterMeterDailyUsage);
            waterMeterFacade.update(mainWaterMeter);
        }, () -> {
            throw ApplicationBaseException.generalErrorException();
        });
    }

    private void checkIfTariffIsPresent(final LocalDate invoiceDate) {
        tariffFacade.findTariffForYearMonth(invoiceDate).orElseThrow(ApplicationBaseException::tariffNotFoundForInvoice);
        tariffFacade.findTariffForYearMonth(invoiceDate.plusMonths(ONE_MONTH)).orElseThrow(ApplicationBaseException::tariffNotFoundForInvoice);
    }

    private void checkIfAllWaterMeterChecksArePresent(final LocalDate invoiceDate) {
        apartmentFacade.findAll().forEach(apartment -> {
            apartment.getWaterMeters().stream().filter(WaterMeter::isActive).forEach(waterMeter -> {
                waterMeterCheckFacade.findWaterMeterCheckByDateAndWaterMeterId(invoiceDate.minusMonths(ONE_MONTH),
                        waterMeter.getId()).orElseThrow(ApplicationBaseException::notAllWaterMeterChecksHaveBeenPerformed);
            });
        });
    }

    private void createNewWaterMeterCheck(final CreateInvoiceDto invoice, final LocalDate invoiceDate, final WaterMeter mainWaterMeter) {
        WaterMeterCheck newCheck = WaterMeterCheck.builder()
                .checkDate(invoiceDate)
                .meterReading(mainWaterMeter.getStartingValue().add(invoice.getWaterUsage()))
                .managerAuthored(true)
                .waterMeter(mainWaterMeter)
                .build();
        waterMeterCheckFacade.create(newCheck);
        updateMainWaterMeterDailyUsage(mainWaterMeter);
    }

    private void createNewWaterMeterCheck(final CreateInvoiceDto invoice, final LocalDate invoiceDate, final WaterMeter mainWaterMeter,
                                          final WaterMeterCheck previousCheck) {
        WaterMeterCheck newCheck = WaterMeterCheck.builder()
                .checkDate(invoiceDate)
                .meterReading(previousCheck.getMeterReading().add(invoice.getWaterUsage()))
                .managerAuthored(true)
                .waterMeter(mainWaterMeter)
                .build();
        waterMeterCheckFacade.create(newCheck);
        updateMainWaterMeterDailyUsage(mainWaterMeter);
    }
}
