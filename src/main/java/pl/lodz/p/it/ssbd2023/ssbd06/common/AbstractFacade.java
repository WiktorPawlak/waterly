package pl.lodz.p.it.ssbd2023.ssbd06.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractFacade<T> implements Facade<T> {

    private Class<T> clazz;

    public AbstractFacade(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract EntityManager getEntityManager();

    @Override
    public T create(T entity) {
        try {
            getEntityManager().persist(entity);
            getEntityManager().flush();
            return entity;
        } catch (PersistenceException e) {
            // placeholder till someone will implement custom exceptions
            log.info("Caught exception", e);
            throw new RuntimeException();
        }
    }

    @Override
    public T update(T entity) {
        try {
            entity = getEntityManager().merge(entity);
            getEntityManager().flush();
            return entity;
        } catch (PersistenceException e) {
            log.info("Caught exception", e);
            throw new RuntimeException();
        }
    }

    @Override
    public void delete(T entity) {
        try {
            getEntityManager().remove(getEntityManager().merge(entity));
            getEntityManager().flush();
        } catch (PersistenceException e) {
            log.info("Caught exception", e);
            throw new RuntimeException();
        }
    }

    @Override
    public T findById(Long id) {
        try {
            return Optional.ofNullable(getEntityManager().find(clazz, id)).orElseThrow(EntityNotFoundException::new);
        } catch (PersistenceException e) {
            log.info("Caught exception", e);
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
        } catch (PersistenceException e) {
            log.info("Caught exception", e);
            throw new RuntimeException();
        }
    }
}
