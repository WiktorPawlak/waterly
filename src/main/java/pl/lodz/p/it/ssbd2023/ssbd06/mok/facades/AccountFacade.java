package pl.lodz.p.it.ssbd2023.ssbd06.mok.facades;

import static pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountState.TO_CONFIRM;
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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.java.Log;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2023.ssbd06.exceptions.interceptors.FacadeExceptionHandler;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.AbstractFacade;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.Account;
import pl.lodz.p.it.ssbd2023.ssbd06.persistence.entities.AccountDetails;
import pl.lodz.p.it.ssbd2023.ssbd06.service.observability.Monitored;

@Log
@Monitored
@RequestScoped
@FacadeExceptionHandler
public class AccountFacade extends AbstractFacade<Account> {

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

    @RolesAllowed({OWNER, FACILITY_MANAGER, ADMINISTRATOR})
    public Optional<Account> findByWaitingEmail(final String email) {
        try {
            TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findByEmailAndWaitingEmail", Account.class);
            accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
            accountTypedQuery.setParameter("email", email);
            return Optional.of(accountTypedQuery.getSingleResult());
        } catch (final NoResultException e) {
            return Optional.empty();
        }
    }

    @RolesAllowed(FACILITY_MANAGER)
    public List<Account> findAllOwnersAccounts() {
        TypedQuery<Account> accountTypedQuery = em.createNamedQuery("Account.findOwners", Account.class);
        accountTypedQuery.setFlushMode(FlushModeType.COMMIT);
        return accountTypedQuery.getResultList();
    }

    @Override
    @PermitAll
    public List<Account> findAll() {
        return super.findAll();
    }

    @RolesAllowed({ADMINISTRATOR})
    public List<Account> findAccounts(final String pattern,
                                      final int page,
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

        if (pattern != null) {
            query.where(cb.or(getFilterByPatternPredicates(pattern, cb, account, join)));
        }

        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    @RolesAllowed({ADMINISTRATOR, FACILITY_MANAGER})
    public List<String> findAccountsNames(final String pattern) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Account> account = query.from(Account.class);
        Join<Account, AccountDetails> join = account.join("accountDetails");
        query.select(join.get("email"));

        if (pattern != null) {
            Predicate emailPredicate = cb.like(cb.upper(join.get("email")), "%" + pattern.toUpperCase() + "%");
            query.where(emailPredicate);
        }

        return getEntityManager().createQuery(query).getResultList();
    }

    @RolesAllowed({ADMINISTRATOR})
    public Long count(final String pattern) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Account> account = query.from(Account.class);
        Join<Account, AccountDetails> join = account.join("accountDetails");

        if (pattern != null) {
            query.where(cb.or(getFilterByPatternPredicates(pattern, cb, account, join)));
        }

        query.select(cb.count(account));

        return em.createQuery(query).getSingleResult();
    }

    @RolesAllowed(FACILITY_MANAGER)
    public Long countNotConfirmedAccounts(final String pattern) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Account> account = query.from(Account.class);
        Join<Account, AccountDetails> join = account.join("accountDetails");

        if (pattern != null) {
            Predicate[] patternPredicate = getFilterByPatternPredicates(pattern, cb, account, join);
            Predicate accountStatePredicate = cb.equal(account.get("accountState"), TO_CONFIRM);
            query.select(cb.count(account))
                    .where(cb.and(cb.or(patternPredicate), accountStatePredicate));
        } else {
            query.select(cb.count(account))
                    .where(cb.equal(account.get("accountState"), TO_CONFIRM));
        }

        return em.createQuery(query).getSingleResult();
    }

    @RolesAllowed({FACILITY_MANAGER})
    public List<Account> findNotConfirmedAccounts(final String pattern,
                                                  final int page,
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

        if (pattern != null) {
            Predicate[] patternPredicate = getFilterByPatternPredicates(pattern, cb, account, join);
            Predicate accountStatePredicate = cb.equal(account.get("accountState"), TO_CONFIRM);
            query.where(cb.and(cb.or(patternPredicate), accountStatePredicate));
        } else {
            query.where(cb.equal(account.get("accountState"), TO_CONFIRM));
        }

        return getEntityManager().createQuery(query)
                .setFirstResult(pageSize * (page - 1))
                .setMaxResults(pageSize)
                .getResultList();
    }

    private Predicate[] getFilterByPatternPredicates(final String pattern, final CriteriaBuilder cb, final Root<Account> account,
                                                     final Join<?, ?> join) {
        Expression<String> fullNameExpression = cb.concat(cb.concat(join.get("firstName"), " "), join.get("lastName"));
        Expression<String> fullNameExpressionReversed = cb.concat(cb.concat(join.get("lastName"), " "), join.get("firstName"));

        String filterPattern = "%" + pattern.toUpperCase() + "%";

        Predicate fullNamePredicate = cb.like(cb.upper(fullNameExpression), filterPattern);
        Predicate fullNamePredicateReversed = cb.like(cb.upper(fullNameExpressionReversed), filterPattern);
        Predicate emailPredicate = cb.like(cb.upper(join.get("email")), filterPattern);
        Predicate loginPredicate = cb.like(cb.upper(account.get("login")), filterPattern);

        return new Predicate[]{fullNamePredicate, fullNamePredicateReversed, emailPredicate, loginPredicate};
    }

    private From<?, ?> resolveFieldClass(final Join<?, ?> join, final String fieldName) {
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
