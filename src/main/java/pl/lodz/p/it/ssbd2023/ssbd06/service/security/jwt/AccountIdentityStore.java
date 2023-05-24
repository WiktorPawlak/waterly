package pl.lodz.p.it.ssbd2023.ssbd06.service.security.jwt;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.CONFIRMED;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.AuthFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.password.BCryptHash;

@Stateless
public class AccountIdentityStore {

    private final Logger log = Logger.getLogger(getClass().getName());

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

        if (!isPasswordValid(credential.getPassword(), account.getPassword()) || !canAuthenticate(account)) {
            log.info("Invalid authentication: Wrong password or account is not active/not confirmed");
            return INVALID_RESULT;
        }

        return new CredentialValidationResult(
                account.getLogin(),
                account.getRoles().stream().filter(Role::isActive).map(Role::getPermissionLevel).collect(Collectors.toSet())
        );
    }

    private boolean isPasswordValid(final String credentialPassword, final String accountPassword) {
        return hashProvider.verify(credentialPassword.toCharArray(), accountPassword);
    }

    private boolean canAuthenticate(final Account account) {
        if (account.isActive() && account.getAccountState() == CONFIRMED) {
            return true;
        } else {
            throw ApplicationBaseException.accountLockedException();
        }
    }

}
