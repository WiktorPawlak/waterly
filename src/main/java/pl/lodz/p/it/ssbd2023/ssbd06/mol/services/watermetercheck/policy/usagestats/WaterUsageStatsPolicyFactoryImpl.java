package pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats;

import static pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.policy.usagestats.WaterUsageStatsPolicy.WATER_METER_CHECK_MONTH_INTERVAL;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.bill.ReadOnlyBillService;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.services.watermetercheck.WaterMeterCheckService;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.WaterMeterCheck;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@RolesAllowed({FACILITY_MANAGER, OWNER})
public class WaterUsageStatsPolicyFactoryImpl implements WaterUsageStatsPolicyFactory {

    @Inject
    private WaterMeterCheckService waterMeterCheckService;
    @Inject
    private ReadOnlyBillService readOnlyBillService;

    @Inject
    @Named("firstWaterMeterCheck")
    private WaterUsageStatsPolicy firstWaterMeterCheckPolicy;
    @Inject
    @Named("newOwner")
    private WaterUsageStatsPolicy newOwnerPolicy;
    @Inject
    @Named("standard")
    private WaterUsageStatsPolicy standardPolicy;

    @Override
    public WaterUsageStatsPolicy createPolicy(final WaterMeterCheck newWaterMeterCheck) {
        WaterUsageStatsPolicy policy;

        final Optional<WaterMeterCheck> previousWaterMeterCheck = findPreviousWaterMeterCheck(newWaterMeterCheck);

        boolean firstCheckForWaterMeter = previousWaterMeterCheck.isEmpty();
        if (firstCheckForWaterMeter) {
            policy = firstWaterMeterCheckPolicy;
        } else if (apartmentOwnerChanged(newWaterMeterCheck, previousWaterMeterCheck.get())) {
            policy = newOwnerPolicy;
        } else {
            policy = standardPolicy;
        }

        return policy;
    }

    private Optional<WaterMeterCheck> findPreviousWaterMeterCheck(final WaterMeterCheck newWaterMeterCheck) {
        final LocalDate previousCheckMonth = newWaterMeterCheck.getCheckDate().minusMonths(WATER_METER_CHECK_MONTH_INTERVAL);
        return waterMeterCheckService
                .findWaterMeterCheckForCheckDate(previousCheckMonth, newWaterMeterCheck.getWaterMeter());
    }

    private boolean apartmentOwnerChanged(final WaterMeterCheck newCheck, final WaterMeterCheck previousWaterMeterCheck) {
        var previousCheckApartmentOwnerId = readOnlyBillService.findBillOwnerIdByApartmentAndDate(
                previousWaterMeterCheck.getWaterMeter().getApartment(),
                previousWaterMeterCheck.getCheckDate());
        return previousCheckApartmentOwnerId.isPresent() && newCheck.getWaterMeter().getApartmentOwnerId() != previousCheckApartmentOwnerId.get();
    }
}
