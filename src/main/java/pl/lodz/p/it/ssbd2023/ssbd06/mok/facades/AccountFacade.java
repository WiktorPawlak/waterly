package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

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
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountFacade extends AbstractFacade<Account> {

    private final Logger log = Logger.getLogger(getClass().getName());

    @PersistenceContext(unitName = "mokPU")
    private EntityManager em;

    public AccountFacade() {
        super(Account.class);
    }

    @Override
    @PermitAll
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @PermitAll
    public Account create(final Account entity) {
        return super.create(entity);
    }

    @Override
    @PermitAll
    public Account update(final Account entity) {
        return super.update(entity);
    }

    @Override
    @PermitAll
    public Account findById(final Long id) {
        return super.findById(id);
    }

    @PermitAll
    public Account findByLogin(final String login) {
        TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findByLogin", Account.class);
        accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
        accountTypedQuery.setParameter("login", login);
        return accountTypedQuery.getSingleResult();
    }

    @PermitAll
    public Account findByWaitingAccountDetailsId(final long id) {
        TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findByWaitingAccountDetailsUpdates_Id", Account.class);
        accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
        accountTypedQuery.setParameter("id", id);
        return accountTypedQuery.getSingleResult();
    }

    @PermitAll
    public Optional<Account> findByEmail(final String email) {
        try {
            TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findByAccountDetails_Email", Account.class);
            accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
            accountTypedQuery.setParameter("email", email);
            return Optional.of(accountTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION + e);
            throw new RuntimeException();
        }
    }
}
