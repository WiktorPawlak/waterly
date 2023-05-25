package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.CONFIRMED;

import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.PasswordHash;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@Log
@Monitored
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AccountIdentityStore {

    @Inject
    @BCryptHash
    private PasswordHash hashProvider;

    @Inject
    private AuthFacade authFacade;

    public CredentialValidationResult validate(final Credentials credential) {
        var optionalAccount = authFacade.findByLogin(credential.getLogin());
        if (optionalAccount.isEmpty()) {
            throw ApplicationBaseException.authenticationException();
        }
        var account = optionalAccount.get();

        if (!isPasswordValid(credential.getPassword(), account.getPassword())) {
            log.info("Invalid authentication: Wrong password or account is not active/not confirmed");
            return INVALID_RESULT;
        }
        canAuthenticate(account);

        return new CredentialValidationResult(
                account.getLogin(),
                account.getRoles().stream().filter(Role::isActive).map(Role::getPermissionLevel).collect(Collectors.toSet())
        );
    }

    private boolean isPasswordValid(final String credentialPassword, final String accountPassword) {
        return hashProvider.verify(credentialPassword.toCharArray(), accountPassword);
    }

    private void canAuthenticate(final Account account) {
        if (!account.isActive() || account.getAccountState() != CONFIRMED) {
            throw ApplicationBaseException.accountLockedException();
        }
    }

}
