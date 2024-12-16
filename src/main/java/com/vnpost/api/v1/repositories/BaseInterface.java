package com.vnpost.api.v1.repositories;

public interface BaseInterface<T, ID> {
    T findOrFail(ID id, String errorMessage);

    boolean exists(ID id);
}
