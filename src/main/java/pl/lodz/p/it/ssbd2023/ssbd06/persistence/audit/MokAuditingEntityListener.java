package pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit;

import java.security.Principal;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import pl.lodz.p.it.ssbd2023.ssbd06.mok.facades.AccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;

public class MokAuditingEntityListener {

    private Principal securityContext;

    private AccountFacade mokAccountFacade;

    @PrePersist
    public void onPrePersist(final AbstractEntity entity) {
        entity.setCreatedBy(retrieveAuthenticatedAccount());
    }

    @PreUpdate
    public void onPreUpdate(final AbstractEntity entity) {
        entity.setUpdatedBy(retrieveAuthenticatedAccount());
    }

    private Account retrieveAuthenticatedAccount() {
        this.securityContext = CDI.current().select(Principal.class).get();
        this.mokAccountFacade = CDI.current().select(AccountFacade.class).get();
        return mokAccountFacade.findByLogin(securityContext.getName()).orElse(null);
    }
}
