package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import java.util.logging.Logger;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class VerificationTokenFacade extends AbstractFacade<VerificationToken> {

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public VerificationTokenFacade() {
        super(VerificationToken.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PermitAll
    public VerificationToken findByToken(String token) {
        TypedQuery<VerificationToken> verificationTokenTypedQuery = em.createNamedQuery("VerificationToken.findByToken", VerificationToken.class);
        verificationTokenTypedQuery.setFlushMode(FlushModeType.COMMIT);
        verificationTokenTypedQuery.setParameter("token", token);
        return verificationTokenTypedQuery.getSingleResult();
    }

}
