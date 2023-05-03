package pl.lodz.p.it.ssbd2023.ssbd06.persistence;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

public abstract class AbstractFacade<T> {

    private final Class<T> clazz;

    protected AbstractFacade(final Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract EntityManager getEntityManager();

    protected T create(final T entity) {
        getEntityManager().persist(entity);
        getEntityManager().flush();
        return entity;
    }

    protected T update(final T entity) {
        T mergedEnt = getEntityManager().merge(entity);
        getEntityManager().flush();
        return mergedEnt;
    }

    protected void delete(final T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
        getEntityManager().flush();
    }

    protected T findById(final Long id) {
        return Optional.ofNullable(getEntityManager().find(clazz, id)).orElseThrow(EntityNotFoundException::new);
    }

    protected List<T> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        cq.select(cq.from(clazz));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
