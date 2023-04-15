package pl.lodz.p.it.ssbd2023.ssbd06.mok.endpoints;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.services.AccountService;

@LocalBean
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountEndpoint {

    @Inject
    private AccountService accountService;

    @RolesAllowed("ADMINISTRATOR")
    public void changeAccountActiveStatus(final long id, final boolean active) {
        accountService.changeAccountActiveStatus(id, active);
    }
}
