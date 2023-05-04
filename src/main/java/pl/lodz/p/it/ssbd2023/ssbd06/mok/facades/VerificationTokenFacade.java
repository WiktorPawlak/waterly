package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;

@Stateless
@FacadeExceptionHandler
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class VerificationTokenFacade extends AbstractFacade<VerificationToken> {

    private final Logger log = Logger.getLogger(getClass().getName());

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public VerificationTokenFacade() {
        super(VerificationToken.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @PermitAll
    public VerificationToken create(final VerificationToken entity) {
        return super.create(entity);
    }

    @PermitAll
    public Optional<VerificationToken> findValidByToken(final String token) {
        TypedQuery<VerificationToken> verificationTokenTypedQuery = em.createNamedQuery("VerificationToken.findValidByToken", VerificationToken.class);
        verificationTokenTypedQuery.setFlushMode(FlushModeType.COMMIT);
        verificationTokenTypedQuery.setParameter("token", token);
        try {
            return Optional.of(verificationTokenTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @PermitAll
    public List<VerificationToken> findAll() {
        return em.createNamedQuery("VerificationToken.findAll", VerificationToken.class)
                .setFlushMode(FlushModeType.COMMIT)
                .getResultList();
    }

    @PermitAll
    public List<VerificationToken> findByAccountId(final long accountId) {
        return em.createNamedQuery("VerificationToken.findByAccountId", VerificationToken.class)
                .setFlushMode(FlushModeType.COMMIT)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @PermitAll
    public void deleteByAccountId(final long accountId) {
        em.createNamedQuery("VerificationToken.deleteByAccountId")
                .setFlushMode(FlushModeType.COMMIT)
                .setParameter("accountId", accountId)
                .executeUpdate();
    }

    @Override
    @PermitAll
    public void delete(final VerificationToken entity) {
        super.delete(entity);
    }

    @Override
    @PermitAll
    public VerificationToken findById(final Long id) {
        return super.findById(id);
    }

}
