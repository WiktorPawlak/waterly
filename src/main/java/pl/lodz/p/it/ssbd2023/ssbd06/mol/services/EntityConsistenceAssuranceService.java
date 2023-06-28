package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.ConsistencyAssuranceTopic.MAIN_WATER_METER_PERSISTENCE;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.ConsistencyAssuranceTopic.TARIFF_PERSISTENCE;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.ConsistencyAssuranceTopic.WATER_METER_PERSISTENCE;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;

import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.EntityConsistenceAssuranceFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.EntityConsistenceAssurance;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class EntityConsistenceAssuranceService {

    @Inject
    private EntityConsistenceAssuranceFacade entityConsistenceAssuranceFacade;

    @RolesAllowed({FACILITY_MANAGER})
    public void create(final EntityConsistenceAssurance entityConsistenceAssurance) {
        entityConsistenceAssuranceFacade.create(entityConsistenceAssurance);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Optional<EntityConsistenceAssurance> findMainWaterMeterConsistencyAssurance() {
        return entityConsistenceAssuranceFacade.findConsistencyAssuranceByTopic(MAIN_WATER_METER_PERSISTENCE);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Optional<EntityConsistenceAssurance> findTariffConsistencyAssurance() {
        return entityConsistenceAssuranceFacade.findConsistencyAssuranceByTopic(TARIFF_PERSISTENCE);
    }

    @RolesAllowed({FACILITY_MANAGER})
    public Optional<EntityConsistenceAssurance> findWaterMeterConsistencyAssurance(final String uniqueChain) {
        return entityConsistenceAssuranceFacade
                .findConsistencyAssuranceByTopicAndUniqueChain(WATER_METER_PERSISTENCE, uniqueChain);
    }

}
