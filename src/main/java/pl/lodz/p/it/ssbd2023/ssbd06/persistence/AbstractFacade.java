package pl.lodz.p.it.ssbd2023.ssbd06.persistence;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

public abstract class AbstractFacade<T> {

    public static final String CAUGHT_EXCEPTION = "Caught exception";
    private final Logger log = Logger.getLogger(getClass().getName());
    private final Class<T> clazz;

    protected AbstractFacade(final Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract EntityManager getEntityManager();

    protected T create(final T entity) {
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
            return entity;
        } catch (final PersistenceException e) {
            // placeholder till someone will implement custom exceptions
            log.info(CAUGHT_EXCEPTION + e);
            throw new RuntimeException();
        }
    }

    protected T update(final T entity) {
        try {
            T mergedEnt = getEntityManager().merge(entity);
            getEntityManager().flush();
            return mergedEnt;
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION + e);
            throw new RuntimeException();
        }
    }

    protected void delete(final T entity) {
        try {
            getEntityManager().remove(getEntityManager().merge(entity));
            getEntityManager().flush();
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION + e);
            throw new RuntimeException();
        }
    }

    protected T findById(final Long id) {
        try {
            return Optional.ofNullable(getEntityManager().find(clazz, id)).orElseThrow(EntityNotFoundException::new);
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION + e);
            throw new RuntimeException();
        }
    }

    protected List<T> findAll() {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(clazz);
            cq.select(cq.from(clazz));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION + e);
            throw new RuntimeException();
        }
    }
}
