package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentBillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.BillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.OwnerBillDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.MolAccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.ReadOnlyBillService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@RequestScoped
public class BillEndpoint implements Serializable {

    @Inject
    private ReadOnlyBillService readOnlyBillService;
    @Inject
    private MolAccountService molAccountService;

    @RolesAllowed({FACILITY_MANAGER})
    public BillDto getBillDetail(final String date, final long apartmentId) {
        try {
            LocalDate localDate = setFirstDayOfMonth(date);
            Optional<Bill> optionalBill = readOnlyBillService.getBillByDateAndApartmentId(localDate, apartmentId);
            if (optionalBill.isPresent()) {
                return new BillDto(optionalBill.get());
            } else {
                throw ApplicationBaseException.noSuchBillException();
            }
        } catch (final DateTimeParseException e) {
            throw ApplicationBaseException.invalidDateException();
        }
    }

    @RolesAllowed({OWNER})
    public BillDto getBillDetailByOwner(final String date, final long apartmentId) {
        try {
            LocalDate localDate = setFirstDayOfMonth(date);
            Optional<Bill> optionalBill = readOnlyBillService.getBillByDateAndApartmentId(localDate, apartmentId);
            if (optionalBill.isPresent()) {
                checkBillBelongsToOwner(optionalBill.get());
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

    private LocalDate setFirstDayOfMonth(final String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        YearMonth yearMonth = YearMonth.parse(date, formatter);
        return yearMonth.atDay(1);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<ApartmentBillDto> getBillsByApartmentId(final long apartmentId) {
        var bills = readOnlyBillService.getBillsByApartmentId(apartmentId);
        return bills.stream()
                .map(ApartmentBillDto::of)
                .toList();
    }

    @RolesAllowed(OWNER)
    public List<OwnerBillDto> getOwnerBills(final String login) {
        var bills = readOnlyBillService.findOwnerBillsByLogin(login);
        return bills.stream()
                .map(OwnerBillDto::of)
                .toList();
    }
}
