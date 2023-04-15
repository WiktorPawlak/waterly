package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.notifications.NotificationsProvider;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountService {

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private NotificationsProvider notificationsProvider;

    //TODO @RolesAllowed Hierarchia roli we wszystkich klasach/warstwach modułu
    @RolesAllowed("ADMINISTRATOR")
    public void changeAccountActiveStatus(final long id, final boolean active) {
        Account account = accountFacade.findById(id);
        account.setActive(active);
        //TODO set incorrectAuthenticationAttemptsCount to 0
        accountFacade.update(account);
        notificationsProvider.notifyAccountActiveStatusChanged(id);
    }
}
