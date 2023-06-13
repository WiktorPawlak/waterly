package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.policy;

import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.GenerateBillsService.ONE_MONTH;

import java.time.LocalDate;
import java.util.Optional;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.BillFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.InvoiceFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Invoice;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class BillPolicyResolver implements BillGenerationPolicyFactory {

    public static final int FIRST_DAY = 1;
    @Inject
    @Named("firstGeneration")
    private BillGenerationPolicy firstBillGenerationPolicy;

    @Inject
    @Named("standardBillGeneration")
    private BillGenerationPolicy standardBillGenerationPolicy;

    @Inject
    @Named("updateForecastPolicy")
    private BillGenerationPolicy updateForecastPolicy;

    @Inject
    private BillFacade billFacade;

    @Inject
    private InvoiceFacade invoiceFacade;

    @Override
    public Tuple2<BillGenerationPolicy, Bill> resolveBillGenerationPolicy(final LocalDate date, final Apartment apartment) {
        LocalDate dateResolved = date.withDayOfMonth(FIRST_DAY);
        Optional<Invoice> invoice = invoiceFacade.findInvoiceForYearMonth(dateResolved.plusMonths(ONE_MONTH));
        Optional<Bill> optBill = billFacade.findByDateAndApartmentId(dateResolved, apartment.getId());

        boolean shouldPickFirstGenerationPolicy = invoice.isEmpty() && optBill.isEmpty();
        boolean shouldPickUpdateForecastPolicy = invoice.isEmpty() && optBill.isPresent();
        boolean shouldPickStandardPolicy = invoice.isPresent() && optBill.isPresent();

        if (shouldPickFirstGenerationPolicy) {
            Bill newBill = new Bill();
            newBill.setDate(dateResolved);
            newBill.setAccount(apartment.getOwner());
            newBill.setApartment(apartment);
            return Tuple.of(firstBillGenerationPolicy, newBill);
        }

        if (shouldPickUpdateForecastPolicy) {
            return Tuple.of(updateForecastPolicy, optBill.get());
        }

        if (shouldPickStandardPolicy) {
            return Tuple.of(standardBillGenerationPolicy, optBill.get());
        }

        throw ApplicationBaseException.generalErrorException();

    }
}
