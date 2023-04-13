package pl.lodz.p.it.ssbd2023.ssbd06.mok.services;

import java.time.LocalDateTime;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.service.config.Property;
import pl.lodz.p.it.ssbd2023.ssbd06.service.notifications.NotificationsProvider;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountService {

    @Inject
    private AccountFacade accountFacade;
    @Inject
    private NotificationsProvider notificationsProvider;
    @Inject
    private AccountActivationTimer accountActivationTimer;

    @Inject
    @Property("auth.attempts")
    private int authAttempts;

    @PermitAll
    public boolean checkAccountActive(final String login) {
        return accountFacade.findByLogin(login).isActive();
    }

    @PermitAll
    public void changeAccountActiveStatus(final long id, final boolean active) {
        Account account = accountFacade.findById(id);
        account.setActive(active);
        account.getAuthInfo().setIncorrectAuthCount(0);
        accountFacade.update(account);
        notificationsProvider.notifyAccountActiveStatusChanged(id);
    }

    @PermitAll
    public void updateSuccessfulAuthInfo(final LocalDateTime authenticationDate, final String login, final String ipAddress) {
        var account = accountFacade.findByLogin(login);
        var accountAuthInfo = account.getAuthInfo();

        accountAuthInfo.setIncorrectAuthCount(0);
        accountAuthInfo.setLastSuccessAuth(authenticationDate);
        accountAuthInfo.setLastIpAddress(ipAddress);

        accountFacade.update(account);
    }

    @PermitAll
    public void updateFailedAuthInfo(final LocalDateTime authenticationDate, final String login) {
        var account = accountFacade.findByLogin(login);
        var accountAuthInfo = account.getAuthInfo();
        var authAttemptsCount = accountAuthInfo.getIncorrectAuthCount();

        accountAuthInfo.setIncorrectAuthCount(authAttemptsCount + 1);
        accountAuthInfo.setLastIncorrectAuth(authenticationDate);

        if (++authAttemptsCount == authAttempts) {
            this.changeAccountActiveStatus(account.getId(), false);
            accountActivationTimer.scheduleAccountActivation(account.getId());
        }

        accountFacade.update(account);
    }

    @PermitAll
    public void updateOwnAccountDetails(final String login, final AccountDetails accountDetails) {
        Account account = accountFacade.findByLogin(login);
        String currentAccountEmail = account.getAccountDetails().getEmail();

        if (currentAccountEmail.equalsIgnoreCase(accountDetails.getEmail())) {
            updateAccountDetails(accountDetails, account);
        } else {
            account.setWaitingAccountDetails(accountDetails);
            accountFacade.update(account);
            notificationsProvider.notifyWaitingAccountDetailsUpdate(account.getId());
        }
    }

    @PermitAll
    public void resendEmailToAcceptAccountDetailsUpdate(final String login) {
        Account account = accountFacade.findByLogin(login);

        notificationsProvider.notifyWaitingAccountDetailsUpdate(account.getId());
    }

    public void acceptAccountDetailsUpdate(final long id) {
        Account account = accountFacade.findByWaitingAccountDetailsId(id);

        account.setAccountDetails(account.getWaitingAccountDetails());
        account.setWaitingAccountDetails(null);

        accountFacade.update(account);
    }

    private void updateAccountDetails(final AccountDetails newAccountDetails, final Account account) {
        AccountDetails currentAccountDetails = account.getAccountDetails();

        currentAccountDetails.setFirstName(newAccountDetails.getFirstName());
        currentAccountDetails.setLastName(newAccountDetails.getLastName());
        currentAccountDetails.setPhoneNumber(newAccountDetails.getPhoneNumber());

        accountFacade.update(account);
    }

}
