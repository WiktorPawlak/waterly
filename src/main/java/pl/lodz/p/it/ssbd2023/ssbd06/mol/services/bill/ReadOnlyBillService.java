package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.BillFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Bill;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@RequestScoped
public class ReadOnlyBillService {

    @Inject
    private BillFacade billFacade;

    @RolesAllowed({OWNER, FACILITY_MANAGER})
    public Optional<Bill> getBillByDateAndApartmentId(final LocalDate date, final long apartmentId) {
        return billFacade.findByDateAndApartmentId(date, apartmentId);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public List<Bill> getBillsByApartmentId(final long apartmentId) {
        return billFacade.findByApartmentId(apartmentId);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Optional<Long> findBillOwnerIdByApartmentAndDate(final Apartment apartment, final LocalDate billDate) {
        return billFacade.findBillOwnerIdByApartmentIdAndDate(apartment.getId(), billDate);
    }

    @RolesAllowed(OWNER)
    public List<Bill> findOwnerBillsByLogin(final String login) {
        return billFacade.findBillsByOwnerLogin(login);
    }
}
