package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotSupportedException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ApartmentFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ApartmentService {

    @Inject
    ApartmentFacade apartmentFacade;

    @RolesAllowed({FACILITY_MANAGER})
    public void createApartment(final Apartment apartment) {
        apartmentFacade.create(apartment);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateApartment(final long id, final EditApartmentDetailsDto dto) {
        apartmentFacade.findById(id);
        // merge changes
        apartmentFacade.update(new Apartment());
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void changeApartmentOwner(final long id, final EditApartmentDetailsDto dto) {
        apartmentFacade.findById(id);
        apartmentFacade.update(new Apartment());
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void deleteApartmentOwner(final long id) {
        throw new NotSupportedException();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void addApartmentOwner(final long id, final ChangeApartmentOwnerDto dto) {
        apartmentFacade.findById(id);
        apartmentFacade.update(new Apartment());
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Apartment> getAllAccounts() {
        return apartmentFacade.findAll();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Apartment> getOwnerAllAccounts(final long ownerId) {
        return apartmentFacade.findOwnerAllApartments(ownerId);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Apartment getApartmentById(final long apartmentId) {
        return apartmentFacade.findById(apartmentId);
    }
}
