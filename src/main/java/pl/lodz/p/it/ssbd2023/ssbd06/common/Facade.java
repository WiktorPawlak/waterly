package pl.lodz.p.it.ssbd2023.ssbd06.common;

import java.util.List;

public interface Facade<T> {

    T create(T entity);

    T update(T entity);

    void delete(T entity);

    T findById(Long id);

    List<T> findAll();

}
