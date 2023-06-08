package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.BillFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class BillService {

    @Inject
    private BillFacade billFacade;

    @RolesAllowed({OWNER})
    public Optional<Bill> getBillsByOwnerId(final long ownerId, final LocalDate date) {
        return billFacade.findByOwnerId(ownerId, date);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<Bill> getBillsByApartmentId(final long apartmentId) {
        return billFacade.findByApartmentId(apartmentId);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Bill getBillById(final long billId) {
        return billFacade.findById(billId);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<Long> findBillOwnerIdByApartmentAndDate(final Apartment apartment, final LocalDate billDate) {
        return billFacade.findBillOwnerIdByApartmentIdAndDate(apartment.getId(), billDate);
    }

}
