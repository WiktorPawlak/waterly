package pl.lodz.p.it.ssbd2023.ssbd06.common;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractFacade<T> implements Facade<T> {

    public static final String CAUGHT_EXCEPTION = "Caught exception";
    private final Class<T> clazz;

    protected AbstractFacade(final Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract EntityManager getEntityManager();

    @Override
    public T create(final T entity) {
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
            return entity;
        } catch (final PersistenceException e) {
            // placeholder till someone will implement custom exceptions
            log.info(CAUGHT_EXCEPTION, e);
            throw new RuntimeException();
        }
    }

    @Override
    public T update(final T entity) {
        try {
            T mergedEnt = getEntityManager().merge(entity);
            getEntityManager().flush();
            return mergedEnt;
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION, e);
            throw new RuntimeException();
        }
    }

    @Override
    public void delete(final T entity) {
        try {
            getEntityManager().remove(getEntityManager().merge(entity));
            getEntityManager().flush();
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION, e);
            throw new RuntimeException();
        }
    }

    @Override
    public T findById(final Long id) {
        try {
            return Optional.ofNullable(getEntityManager().find(clazz, id)).orElseThrow(EntityNotFoundException::new);
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION, e);
            throw new RuntimeException();
        }
    }

    @Override
    public List<T> findAll() {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(clazz);
            cq.select(cq.from(clazz));
            return getEntityManager().createQuery(cq).getResultList();
        } catch (final PersistenceException e) {
            log.info(CAUGHT_EXCEPTION, e);
            throw new RuntimeException();
        }
    }
}
