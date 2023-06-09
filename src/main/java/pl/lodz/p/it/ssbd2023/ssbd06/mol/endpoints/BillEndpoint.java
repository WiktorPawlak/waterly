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
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.BillService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class BillEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private BillService billService;

    @RolesAllowed({OWNER})
    public BillDto getBillsByOwnerId(final long ownerId, final String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(date, formatter);
            LocalDate localDate = yearMonth.atDay(1);
            Optional<Bill> optionalBill = billService.getBillsByOwnerId(ownerId, localDate);
            if (optionalBill.isPresent()){
                return new BillDto(optionalBill.get());
            } else {
                throw ApplicationBaseException.noSuchBillException();
            }
        } catch (final DateTimeParseException e) {
            throw ApplicationBaseException.invalidDateException();
        }
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<ApartmentBillsDto> getBillsByApartmentId(final long apartmentId) {
        billService.getBillsByApartmentId(apartmentId);
        return Collections.emptyList();
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public BillDto getBillById(final long billId) {
        billService.getBillById(billId);
        return null;
    }
}
