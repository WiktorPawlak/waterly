package pl.lodz.p.it.ssbd2023.ssbd06.mol.endpoints;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;

import io.vavr.Tuple2;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.TransactionRollbackInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.dto.PaginatedList;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.config.PaginationConfig;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.ChangeApartmentOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.CreateApartmentDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.dto.EditApartmentDetailsDto;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.ApartmentService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.MolAccountService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.WaterMeterService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Apartment;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.TransactionBoundariesTracingEndpoint;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthenticatedAccount;
import pl.lodz.p.it.ssbd2023.ssbd06.service.time.TimeProvider;

@TransactionRollbackInterceptor
@Monitored
@LocalBean
@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ApartmentEndpoint extends TransactionBoundariesTracingEndpoint {

    @Inject
    private MolAccountService molAccountService;

    @Inject
    private ApartmentService apartmentService;

    @Inject
    private PaginationConfig paginationConfig;

    @Inject
    private AuthenticatedAccount authenticatedAccount;

    @Inject
    private WaterMeterService waterMeterService;

    @Inject
    private TimeProvider timeProvider;

    @RolesAllowed(FACILITY_MANAGER)
    public void createApartment(final CreateApartmentDto dto) {
        Account ownerAccount = molAccountService.getOwnerAccountById(dto.getOwnerId());
        Apartment apartment = apartmentService.createApartment(dto.toDomain(ownerAccount));
        //waterMeterService.assignWaterMeter(apartment, new AssignWaterMeterDto()); TODO

    }

    @RolesAllowed({FACILITY_MANAGER})
    public void updateApartment(final long id, final EditApartmentDetailsDto dto) {
        apartmentService.updateApartment(id, dto);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public PaginatedList<ApartmentDto> getAllApartments(final String pattern,
                                                        final Integer page,
                                                        final Integer pageSize,
                                                        final String order,
                                                        final String orderBy) {
        int preparedPage = paginationConfig.preparePage(page);
        int preparedPageSize = paginationConfig.preparePageSize(pageSize);
        String preparedOrderBy = orderBy != null ? orderBy : "number";
        String preparedPattern = paginationConfig.preparePattern(pattern);
        boolean ascOrder = paginationConfig.prepareAscOrder(order);

        Tuple2<List<Apartment>, Long> paginatedApartments =
                apartmentService.getAllApartments(preparedPattern, preparedPage, preparedPageSize, ascOrder, preparedOrderBy);

        List<ApartmentDto> apartmentDtos = paginatedApartments._1
                .stream().map(ApartmentDto::new)
                .toList();
        return new PaginatedList<>(
                apartmentDtos,
                preparedPage,
                apartmentDtos.size(),
                (long) Math.ceil(paginatedApartments._2.doubleValue() / preparedPageSize)
        );
    }

    @RolesAllowed(OWNER)
    public PaginatedList<ApartmentDto> getSelfApartments(final String pattern,
                                                         final Integer page,
                                                         final Integer pageSize,
                                                         final String order,
                                                         final String orderBy) {
        Account account = molAccountService.getAccountByLogin(authenticatedAccount.getLogin());

        int preparedPage = paginationConfig.preparePage(page);
        int preparedPageSize = paginationConfig.preparePageSize(pageSize);
        String preparedOrderBy = orderBy != null ? orderBy : "number";
        String preparedPattern = paginationConfig.preparePattern(pattern);
        boolean ascOrder = paginationConfig.prepareAscOrder(order);


        Tuple2<List<Apartment>, Long> paginatedApartments =
                apartmentService.getOwnerAllApartaments(account, preparedPattern, preparedPage, preparedPageSize, ascOrder, preparedOrderBy);

        List<ApartmentDto> apartmentDtos = paginatedApartments._1
                .stream().map(ApartmentDto::new)
                .toList();

        return new PaginatedList<>(
                apartmentDtos,
                preparedPage,
                apartmentDtos.size(),
                (long) Math.ceil(paginatedApartments._2.doubleValue() / preparedPageSize)
        );
    }

    @RolesAllowed({FACILITY_MANAGER})
    public ApartmentDto getApartmentById(final long apartmentId) {
        return new ApartmentDto(apartmentService.getApartmentById(apartmentId));
    }

    @RolesAllowed({FACILITY_MANAGER})
    public void changeApartmentOwner(final long id, final ChangeApartmentOwnerDto dto) {
        apartmentService.changeApartmentOwner(id, dto);
    }
}


