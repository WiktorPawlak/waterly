package pl.lodz.p.it.ssbd2023.ssbd06.service.security;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.CONFIRMED;

import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Role;

@Stateless
public class AccountIdentityStore {

    private final Logger log = Logger.getLogger(getClass().getName());

    @Inject
    private AuthFacade authFacade;

    public CredentialValidationResult validate(final Credentials credential) {
        try {
            Account account = authFacade.findByLogin(credential.getLogin());

            if (isPasswordValid(credential.getPassword(), account.getPassword()) && isAccountActive(account)) {
                return new CredentialValidationResult(
                        account.getLogin(),
                        account.getRoles().stream().map(Role::getPermissionLevel).collect(Collectors.toSet())
                );
            } else {
                log.info("Invalid authentication: Wrong password or account is not active");
                return INVALID_RESULT;
            }
        } catch (final Exception e) {
            //TODO adjust exception when custom exception will be introduce
            log.info("Invalid authentication: Account not found");
            return INVALID_RESULT;
        }
    }

    private boolean isPasswordValid(final String credentialPassword, final String accountPassword) {
        //TODO password hash
        return credentialPassword.equals(accountPassword);
    }

    private boolean isAccountActive(final Account account) {
        //TODO może jakiś exception który będzie mówił że towje konto jest jeszcze nie zakceptowane
        return account.isActive() && account.getAccountState() == CONFIRMED;
    }

}
