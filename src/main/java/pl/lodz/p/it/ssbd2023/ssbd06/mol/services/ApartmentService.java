package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.exceptions.OwnerAccountDoesNotExistException;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ApartmentFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ReadOnlyAccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ApartmentService {

    @Inject
    ApartmentFacade apartmentFacade;

    @Inject
    ReadOnlyAccountFacade accountFacade;


    @RolesAllowed(FACILITY_MANAGER)
    public Apartment createApartment(final Apartment apartment) {
        return apartmentFacade.create(apartment);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void updateApartment(final long id, final EditApartmentDetailsDto dto) {
        Apartment apartment = apartmentFacade.findById(id);
        apartment.setArea(dto.getArea());
        apartment.setNumber(dto.getNumber());
        apartmentFacade.update(apartment);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void changeApartmentOwner(final long id, final ChangeApartmentOwnerDto dto) {
        addApartmentOwner(id, dto);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public void addApartmentOwner(final long apartmentId, final ChangeApartmentOwnerDto dto) {
        Apartment apartment = apartmentFacade.findById(apartmentId);
        Account newOwner = accountFacade.findByLogin(dto.getNewOwnerLogin())
                .orElseThrow(OwnerAccountDoesNotExistException::ownerAccountDoesNotExistException);

        apartment.setOwner(newOwner);
        apartmentFacade.update(apartment);
    }


    @RolesAllowed(FACILITY_MANAGER)
    public Tuple2<List<Apartment>, Long> getAllApartments(final String pattern,
                                                          final int page,
                                                          final int pageSize,
                                                          final boolean ascOrder,
                                                          final String orderBy) {
        return Tuple.of(
                apartmentFacade.findApartments(pattern, page, pageSize, ascOrder, orderBy),
                apartmentFacade.countAll(pattern)
        );
    }

    @RolesAllowed(OWNER)
    public Tuple2<List<Apartment>, Long> getOwnerAllApartaments(final Account account,
                                                                final String pattern,
                                                                final int page,
                                                                final int pageSize,
                                                                final boolean ascOrder,
                                                                final String orderBy) {
        return Tuple.of(
                apartmentFacade.findOwnerAllApartments(account.getId(), pattern, page, pageSize, ascOrder, orderBy),
                apartmentFacade.countAllOwnerApartments(account.getId(), pattern)
        );
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Apartment getApartmentById(final long apartmentId) {
        return apartmentFacade.findById(apartmentId);
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Apartment findApartmentByWaterMeterId(final long waterMeterId) {
        return apartmentFacade.findByWaterMeterId(waterMeterId);
    }

}
