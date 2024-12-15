package com.vnpost.main.repositories;

public interface BaseInterface<T, ID> {
    T findOrFail(ID id, String errorMessage);

    boolean exists(ID id);
}
