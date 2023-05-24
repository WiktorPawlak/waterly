package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.AssignWaterMeterDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.ApartmentService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterMeterService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ApartmentEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    ApartmentService apartmentService;

    @Inject
    private WaterMeterService waterMeterService;

    @RolesAllowed({FACILITY_MANAGER})
    public void createApartment(final CreateApartmentDto dto) {
        //dto -> Apartment
        waterMeterService.assignWaterMeter(new Apartment(), new AssignWaterMeterDto());
        apartmentService.createApartment(new Apartment());
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateApartment(final long id, final EditApartmentDetailsDto dto) {
        apartmentService.updateApartment(id, dto);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<ApartmentsDto> getOwnerAllAccounts() {
        List<Apartment> apartments = apartmentService.getAllAccounts();
        return List.of();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<ApartmentsDto> getOwnerAllAccounts(final long ownerId) {
        List<Apartment> apartments = apartmentService.getOwnerAllAccounts(ownerId);
        return List.of();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Apartment getApartmentById(final long apartmentId) {
        return apartmentService.getApartmentById(apartmentId);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void changeApartmentOwner(final long id, final ChangeApartmentOwnerDto dto) {
        apartmentService.deleteApartmentOwner(id);
        apartmentService.addApartmentOwner(id, dto);
    }

}


