package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill;

import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService.ONE_MONTH;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.COLD_WATER;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterType.HOT_WATER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.vavr.Tuple2;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.WaterMeterCheckDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckAddedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.events.WaterMeterCheckUpdatedEvent;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ApartmentFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.BillFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.InvoiceFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.TariffFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterMeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.WaterUsageStatsFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy.BillGenerationPolicy;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy.BillGenerationPolicyFactory;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Tariff;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.UsageReport;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeter;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterUsageStats;

public abstract class AbstractBillService {
    public static final int ROUNDING_MODE = 2;
    @Inject
    protected BillFacade billFacade;

    @Inject
    protected ApartmentFacade apartmentFacade;

    @Inject
    protected TariffFacade tariffFacade;

    @Inject
    protected WaterMeterFacade waterMeterFacade;

    @Inject
    protected InvoiceFacade invoiceFacade;

    @Inject
    protected WaterUsageStatsFacade waterUsageStatsFacade;

    @Inject
    protected BillGenerationPolicyFactory billGenerationPolicyFactory;

    protected static UsageReport createAndFillRealUsage(final Bill bill,
                                                        final BigDecimal totalApartmentsArea,
                                                        final BigDecimal unbilledWaterAmount,
                                                        final Tariff tariffForInvoice,
                                                        final WaterUsageStats usageStatsForBill) {
        UsageReport realUsage = new UsageReport();
        realUsage.setColdWaterUsage(usageStatsForBill.getColdWaterUsage());
        realUsage.setColdWaterCost(usageStatsForBill.getColdWaterUsage().multiply(tariffForInvoice.getColdWaterPrice()));
        realUsage.setHotWaterUsage(usageStatsForBill.getHotWaterUsage());
        realUsage.setHotWaterCost(usageStatsForBill.getHotWaterUsage().multiply(tariffForInvoice.getColdWaterPrice().add(tariffForInvoice.getHotWaterPrice())));
        realUsage.setGarbageCost(usageStatsForBill.getColdWaterUsage().add(usageStatsForBill.getHotWaterUsage()).multiply(tariffForInvoice.getTrashPrice()));
        BigDecimal unbilledWaterAmountForThisApartment = bill.getApartment().getArea().divide(totalApartmentsArea, ROUNDING_MODE).multiply(unbilledWaterAmount);
        realUsage.setUnbilledWaterAmount(unbilledWaterAmountForThisApartment);
        realUsage.setUnbilledWaterCost(unbilledWaterAmountForThisApartment.multiply(tariffForInvoice.getColdWaterPrice()));
        return realUsage;
    }

    protected static void updateRealUsage(final Bill bill,
                                          final UsageReport realUsage,
                                          final BigDecimal totalApartmentsArea,
                                          final BigDecimal unbilledWaterAmount,
                                          final Tariff tariffForInvoice,
                                          final WaterUsageStats usageStatsForBill) {
        realUsage.setColdWaterUsage(usageStatsForBill.getColdWaterUsage());
        realUsage.setColdWaterCost(usageStatsForBill.getColdWaterUsage().multiply(tariffForInvoice.getColdWaterPrice()));
        realUsage.setHotWaterUsage(usageStatsForBill.getHotWaterUsage());
        realUsage.setHotWaterCost(usageStatsForBill.getHotWaterUsage().multiply(tariffForInvoice.getColdWaterPrice().add(tariffForInvoice.getHotWaterPrice())));
        realUsage.setGarbageCost(usageStatsForBill.getColdWaterUsage().add(usageStatsForBill.getHotWaterUsage()).multiply(tariffForInvoice.getTrashPrice()));
        BigDecimal unbilledWaterAmountForThisApartment = bill.getApartment().getArea().divide(totalApartmentsArea, ROUNDING_MODE).multiply(unbilledWaterAmount);
        realUsage.setUnbilledWaterAmount(unbilledWaterAmountForThisApartment);
        realUsage.setUnbilledWaterCost(unbilledWaterAmountForThisApartment.multiply(tariffForInvoice.getColdWaterPrice()));
    }

    protected Bill createForecastForNextMonth(final Bill bill) {
        Bill newBill = initNewBill(bill);
        Tariff tariffForBill = findTariffForBill(newBill);

        List<WaterMeter> waterMetersForApartment = bill.getApartment().getWaterMeters();
        BigDecimal hotWaterWaterMetersUsage = calculateHotWaterUsageForForecast(newBill, waterMetersForApartment);
        BigDecimal coldWaterMetersUsage = calculateColdWaterUsageForForecast(newBill, waterMetersForApartment);

        WaterUsageStats forecast = createForecastForNextMonth(newBill, hotWaterWaterMetersUsage, coldWaterMetersUsage);

        UsageReport advanceUsage = createAndFillAdvanceUsage(tariffForBill, forecast);
        newBill.setAdvanceUsage(advanceUsage);

        return newBill;
    }

    protected static UsageReport createAndFillAdvanceUsage(final Tariff tariffForBill, final WaterUsageStats forecast) {
        UsageReport advanceUsage = new UsageReport();
        advanceUsage.setColdWaterUsage(forecast.getColdWaterUsage());
        advanceUsage.setColdWaterCost(forecast.getColdWaterUsage().multiply(tariffForBill.getColdWaterPrice()));
        advanceUsage.setHotWaterUsage(forecast.getHotWaterUsage());
        advanceUsage.setHotWaterCost(forecast.getHotWaterUsage().multiply(tariffForBill.getColdWaterPrice().add(tariffForBill.getHotWaterPrice())));
        advanceUsage.setGarbageCost(forecast.getColdWaterUsage().add(advanceUsage.getHotWaterUsage()).multiply(tariffForBill.getTrashPrice()));
        return advanceUsage;
    }

    protected static void updateAdvanceUsage(final UsageReport advanceUsage, final Tariff tariffForBill, final WaterUsageStats forecast) {
        advanceUsage.setColdWaterUsage(forecast.getColdWaterUsage());
        advanceUsage.setColdWaterCost(forecast.getColdWaterUsage().multiply(tariffForBill.getColdWaterPrice()));
        advanceUsage.setHotWaterUsage(forecast.getHotWaterUsage());
        advanceUsage.setHotWaterCost(forecast.getHotWaterUsage().multiply(tariffForBill.getColdWaterPrice().add(tariffForBill.getHotWaterPrice())));
        advanceUsage.setGarbageCost(forecast.getColdWaterUsage().add(advanceUsage.getHotWaterUsage()).multiply(tariffForBill.getTrashPrice()));
    }

    protected static BigDecimal calculateColdWaterUsageForForecast(final Bill newBill, final List<WaterMeter> waterMetersForApartment) {
        return waterMetersForApartment.stream()
                .filter(waterMeter -> waterMeter.getType().equals(COLD_WATER))
                .map(WaterMeter::getExpectedDailyUsage)
                .map(usage -> usage.multiply(
                        BigDecimal.valueOf(newBill.getDate().lengthOfMonth())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected static BigDecimal calculateHotWaterUsageForForecast(final Bill newBill, final List<WaterMeter> waterMetersForApartment) {
        return waterMetersForApartment.stream()
                .filter(waterMeter -> waterMeter.getType().equals(HOT_WATER))
                .map(WaterMeter::getExpectedDailyUsage)
                .map(usage -> usage.multiply(
                        BigDecimal.valueOf(newBill.getDate().lengthOfMonth())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected static Bill initNewBill(final Bill oldBill) {
        Bill newBill = new Bill();
        newBill.setApartment(oldBill.getApartment());
        newBill.setAccount(oldBill.getAccount());
        newBill.setDate(oldBill.getDate().plusMonths(1));
        return newBill;
    }

    protected Tariff findTariffForBill(final Bill bill) {
        return tariffFacade.findTariffForYearMonth(bill.getDate()).orElseThrow();
    }

    protected Tariff findTariffForDate(final LocalDate date) {
        return tariffFacade.findTariffForYearMonth(date).orElseThrow();
    }

    protected WaterUsageStats createForecastForNextMonth(final Bill bill, final BigDecimal hotWaterWaterMetersUsage, final BigDecimal coldWaterMetersUsage) {
        WaterUsageStats forecast = new WaterUsageStats();
        forecast.setApartment(bill.getApartment());
        forecast.setYearMonth(YearMonth.from(bill.getDate()));
        forecast.setColdWaterUsage(coldWaterMetersUsage);
        forecast.setHotWaterUsage(hotWaterWaterMetersUsage);

        return waterUsageStatsFacade.create(forecast);
    }

    protected static BigDecimal calculateTotalApartmentsArea(final List<Apartment> apartmentList) {
        return apartmentList.stream()
                .map(Apartment::getArea)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected List<WaterUsageStats> findAllApartmentsLastMonthUsageStats(final LocalDate invoiceDate, final List<Apartment> apartmentList) {
        return apartmentList.stream()
                .map(apartment -> waterUsageStatsFacade.findByApartmentIdAndYearMonth(apartment.getId(), YearMonth.from(invoiceDate.minusMonths(1))))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    protected static BigDecimal calculateTotalUnbilledWaterAmount(final Invoice invoice, final BigDecimal totalWaterUsageFromLastMonth) {
        return invoice.getWaterUsage().subtract(totalWaterUsageFromLastMonth);
    }

    protected static BigDecimal calculateLastMonthTotalWaterUsage(final List<WaterUsageStats> usageStats) {
        return usageStats.stream()
                .map(apartmentUsage -> apartmentUsage.getColdWaterUsage().add(apartmentUsage.getHotWaterUsage()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected void performBillUpdate(final Bill billToUpdate) {
        Optional<Invoice> invoice = invoiceFacade.findInvoiceForYearMonth(billToUpdate.getDate().plusMonths(ONE_MONTH));
        invoice.ifPresentOrElse(foundInvoice -> {
            throw ApplicationBaseException.generalErrorException();
        }, () -> {
            Tariff tariffForBill = findTariffForBill(billToUpdate);
            WaterUsageStats usageStatsForBill =
                    waterUsageStatsFacade.findByApartmentIdAndYearMonth(billToUpdate.getApartment().getId(), YearMonth.from(billToUpdate.getDate()))
                            .orElseThrow();
            UsageReport forecast = billToUpdate.getAdvanceUsage();
            updateAdvanceUsage(forecast, tariffForBill, usageStatsForBill);
            billFacade.update(billToUpdate);
        });
    }

    protected void performBillUpdate(final Bill billToUpdate, final BigDecimal totalApartemnsArea, final BigDecimal totalUnbilledWaterAmount) {
        Tariff tariffForBill = findTariffForBill(billToUpdate);
        WaterUsageStats usageStatsForBill =
                waterUsageStatsFacade.findByApartmentIdAndYearMonth(billToUpdate.getApartment().getId(), YearMonth.from(billToUpdate.getDate())).orElseThrow();
        UsageReport realUsage = billToUpdate.getRealUsage() != null ? billToUpdate.getRealUsage() : new UsageReport();
        updateRealUsage(billToUpdate, realUsage, totalApartemnsArea, totalUnbilledWaterAmount, tariffForBill, usageStatsForBill);
        billToUpdate.setRealUsage(realUsage);
        billToUpdate.setBalance(calculateTotalBillBalance(billToUpdate));
        billFacade.update(billToUpdate);
    }

    protected BigDecimal calculateTotalBillBalance(final Bill bill) {
        BigDecimal coldWaterBalance = bill.getAdvanceUsage().getColdWaterCost().subtract(bill.getRealUsage().getColdWaterCost());
        BigDecimal hotWaterBalance = bill.getAdvanceUsage().getHotWaterCost().subtract(bill.getRealUsage().getHotWaterCost());
        BigDecimal trashBalance = bill.getAdvanceUsage().getGarbageCost().subtract(bill.getRealUsage().getGarbageCost());
        return coldWaterBalance.add(hotWaterBalance).add(trashBalance);
    }

    protected void selectAndPerformPolicyOperations(final LocalDate date,
                                                    final Apartment apartment,
                                                    final BigDecimal totalApartmentsArea,
                                                    final BigDecimal totalWaterUsage) {
        Tuple2<BillGenerationPolicy, Bill> result = billGenerationPolicyFactory.resolveBillGenerationPolicy(date, apartment);
        BillGenerationPolicy resolvedPolicy = result._1();
        Bill billForApartment = result._2();
        Tariff tariffForBills = findTariffForDate(date);
        resolvedPolicy.performBillOperations(billForApartment, totalApartmentsArea, totalWaterUsage, tariffForBills);
    }

    protected static List<Long> convertDtoToWaterMeterIds(final WaterMeterCheckAddedEvent event) {
        return event.getWaterMeterChecksDto().getWaterMeterChecks()
                .stream()
                .map(WaterMeterCheckDto::getWaterMeterId)
                .toList();
    }

    protected static List<Long> convertDtoToWaterMeterIds(final WaterMeterCheckUpdatedEvent event) {
        return event.getWaterMeterChecksDto().getWaterMeterChecks()
                .stream()
                .map(WaterMeterCheckDto::getWaterMeterId)
                .toList();
    }
}
