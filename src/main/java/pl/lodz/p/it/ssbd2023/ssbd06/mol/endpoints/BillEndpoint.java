package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentBillsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.MolAccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.ReadOnlyBillService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class BillEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private ReadOnlyBillService readOnlyBillService;
    @Inject
    private MolAccountService molAccountService;
    @Inject
    private AuthenticatedAccount callerContext;

    @RolesAllowed({OWNER, FACILITY_MANAGER})
    public BillDto getBillDetail(final String date, final long apartmentId) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(date, formatter);
            LocalDate localDate = yearMonth.atDay(1);
            Optional<Bill> optionalBill = readOnlyBillService.getBillByDateAndApartmentId(localDate, apartmentId);
            if (optionalBill.isPresent()) {
                if (!callerContext.isFacilityManager()) {
                    checkBillBelongsToOwner(optionalBill.get());
                }
                return new BillDto(optionalBill.get());
            } else {
                throw ApplicationBaseException.noSuchBillException();
            }
        } catch (final DateTimeParseException e) {
            throw ApplicationBaseException.invalidDateException();
        }
    }

    private void checkBillBelongsToOwner(final Bill bill) {
        if (bill.getAccount().getId() != molAccountService.getPrincipalId()) {
            throw ApplicationBaseException.billDoesNotBelongToOwnerException();
        }
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<ApartmentBillsDto> getBillsByApartmentId(final long apartmentId) {
        readOnlyBillService.getBillsByApartmentId(apartmentId);
        return Collections.emptyList();
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public BillDto getBillById(final long billId) {
        readOnlyBillService.getBillById(billId);
        return null;
    }
}
