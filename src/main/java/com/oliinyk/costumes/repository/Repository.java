package com.oliinyk.costumes.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {
    void save(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    void update(T entity);

    void delete(UUID id);
}
