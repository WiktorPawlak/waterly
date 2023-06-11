package pl.lodz.p.it.ssbd2023.ssbd06.mol.services;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ResourceNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.ServiceExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ReadOnlyAccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@ServiceExceptionHandler
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MolAccountService {

    @Inject
    private ReadOnlyAccountFacade readOnlyAccountFacade;
    @Inject
    private SecurityContext securityContext;

    @RolesAllowed(FACILITY_MANAGER)
    public Account getOwnerAccountById(final long id) {
        return findOwnerById(id);
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public Account getAccountByLogin(final String login) {
        Optional<Account> optionalAccount = readOnlyAccountFacade.findByLogin(login);

        if (optionalAccount.isEmpty()) {
            log.info(() -> "Owner account with login: " + login + " does not exist");
            throw ApplicationBaseException.ownerAccountDoesNotExistException();
        }

        return optionalAccount.get();
    }

    @RolesAllowed({FACILITY_MANAGER, OWNER})
    public long getPrincipalId() {
        return readOnlyAccountFacade.findByLogin(securityContext.getCallerPrincipal().getName()).orElseThrow().getId();
    }

    private Account findOwnerById(final long id) {
        Optional<Account> optionalAccount = findById(id);

        if (optionalAccount.isEmpty() || !optionalAccount.get().inRole(OWNER)) {
            log.info(() -> "Owner account with id: " + id + " does not exist");
            throw ApplicationBaseException.ownerAccountDoesNotExistException();
        }

        return optionalAccount.get();
    }

    private Optional<Account> findById(final long id) {
        try {
            return Optional.of(readOnlyAccountFacade.findById(id));
        } catch (final ResourceNotFoundException e) {
            return Optional.empty();
        }
    }
}
