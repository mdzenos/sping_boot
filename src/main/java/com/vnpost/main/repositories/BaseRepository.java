package com.vnpost.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import jakarta.persistence.EntityNotFoundException;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID>, BaseInterface<T, ID> {
    @Override
    default T findOrFail(ID id, String errorMessage) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    @Override
    default boolean exists(ID id) {
        return existsById(id);
    }
}
