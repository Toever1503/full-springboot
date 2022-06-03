package com.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

public interface IBaseService<T, M, K> {
    List<T> findAll();

    Page<T> findAll(Pageable page);

    Page<T> filter(Pageable page, Specification<T> specs);

    T findById(K id);

    T add(M model);

    List<T> add(List<M> model);

    T update(M model);

    boolean deleteById(K id);

    boolean deleteByIds(List<K> ids);
}
