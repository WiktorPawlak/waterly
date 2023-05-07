package pl.lodz.p.it.ssbd2023.ssbd06.persistence.audit;

import java.security.Principal;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import pl.lodz.p.it.ssbd2023.ssbd06.mol.facades.ReadOnlyAccountFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;

public class MolAuditingEntityListener {

    private Principal securityContext;

    private ReadOnlyAccountFacade molAccountFacade;

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
        this.molAccountFacade = CDI.current().select(ReadOnlyAccountFacade.class).get();
        return molAccountFacade.findByLogin(securityContext.getName()).orElse(null);
    }
}
