package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.ADMINISTRATOR;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.FACILITY_MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd06.service.security.Permission.OWNER;

import java.util.List;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.TokenType;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.VerificationToken;
import pl.lodz.p.it.ssbd2023.ssbd06.service.security.OnlyGuest;

@RequestScoped
@FacadeExceptionHandler
public class VerificationTokenFacade extends AbstractFacade<VerificationToken> {

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public VerificationTokenFacade() {
        super(VerificationToken.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @PermitAll
    public VerificationToken create(final VerificationToken entity) {
        return super.create(entity);
    }

    @PermitAll
    public Optional<VerificationToken> findValidByTokenAndTokenType(final String token, final TokenType tokenType) {
        TypedQuery<VerificationToken> verificationTokenTypedQuery =
                em.createNamedQuery("VerificationToken.findValidByTokenAndTokenType", VerificationToken.class)
                        .setFlushMode(FlushModeType.COMMIT)
                        .setParameter("token", token)
                        .setParameter("tokenType", tokenType);
        try {
            return Optional.of(verificationTokenTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @PermitAll
    public Optional<VerificationToken> findValidByToken(final String token) {
        TypedQuery<VerificationToken> verificationTokenTypedQuery =
                em.createNamedQuery("VerificationToken.findValidByToken", VerificationToken.class)
                        .setFlushMode(FlushModeType.COMMIT)
                        .setParameter("token", token);
        try {
            return Optional.of(verificationTokenTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }


    @Override
    @PermitAll
    public List<VerificationToken> findAll() {
        return em.createNamedQuery("VerificationToken.findAll", VerificationToken.class)
                .setFlushMode(FlushModeType.COMMIT)
                .getResultList();
    }

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public Optional<VerificationToken> findLatestVerificationToken(final long accountId, final TokenType tokenType) {
        TypedQuery<VerificationToken> verificationTokenTypedQuery =
                em.createNamedQuery("VerificationToken.findLatestVerificationToken", VerificationToken.class)
                        .setFlushMode(FlushModeType.COMMIT)
                        .setParameter("accountId", accountId)
                        .setParameter("tokenType", tokenType);
        try {
            return Optional.of(verificationTokenTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @OnlyGuest
    public List<VerificationToken> findByAccountIdAndTokenType(final long accountId, final TokenType tokenType) {
        return em.createNamedQuery("VerificationToken.findByAccountIdAndTokenType", VerificationToken.class)
                .setFlushMode(FlushModeType.COMMIT)
                .setParameter("accountId", accountId)
                .setParameter("tokenType", tokenType)
                .getResultList();
    }

    @PermitAll
    public void deleteByAccountIdAndTokenType(final long accountId, final TokenType tokenType) {
        em.createNamedQuery("VerificationToken.deleteByAccountIdAndTokenType")
                .setFlushMode(FlushModeType.COMMIT)
                .setParameter("accountId", accountId)
                .setParameter("tokenType", tokenType)
                .executeUpdate();
    }

    @Override
    @PermitAll
    public void delete(final VerificationToken entity) {
        super.delete(entity);
    }
}
