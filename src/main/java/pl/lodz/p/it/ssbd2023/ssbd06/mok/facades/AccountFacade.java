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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Monitored
@Stateless
@FacadeExceptionHandler
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountFacade extends AbstractFacade<Account> {

    private final Logger log = Logger.getLogger(AccountFacade.class.getName());

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
    public void delete(final Account entity) {
        super.delete(entity);
    }

    @Override
    @PermitAll
    public Account findById(final Long id) {
        return super.findById(id);
    }

    @PermitAll
    public Optional<Account> findByLogin(final String login) {
        try {
            TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findByLogin", Account.class);
            accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
            accountTypedQuery.setParameter("login", login);
            return Optional.of(accountTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
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
            TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findAccountByEmail", Account.class);
            accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
            accountTypedQuery.setParameter("email", email);
            return Optional.of(accountTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @PermitAll
    public List<Account> findAll() {
        return super.findAll();
    }

    @PermitAll
    public List<Account> findAccounts(final int page,
                                      final int pageSize,
                                      final boolean ascOrder,
                                      final String orderBy) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> account = query.from(Account.class);
        Join<Account, AccountDetails> join = account.join("accountDetails");
        if (ascOrder) {
            query.orderBy(cb.asc(resolveFieldClass(join, orderBy).get(orderBy)));
        } else {
            query.orderBy(cb.desc(resolveFieldClass(join, orderBy).get(orderBy)));
        }

        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    @PermitAll
    public Long count() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Account> table = query.from(Account.class);
        query.select(criteriaBuilder.count(table));

        return em.createQuery(query).getSingleResult();
    }

    private From<?, ?> resolveFieldClass(final Join<Account, AccountDetails> join, final String fieldName) {
        return switch (fieldName) {
            case "login", "active", "accountState" -> join.getParent();
            case "email", "firstName", "lastName", "phoneNumber" -> join;
            default -> {
                log.severe(() -> "Error, trying to query by invalid field");
                throw ApplicationBaseException.generalErrorException();
            }
        };
    }
}
