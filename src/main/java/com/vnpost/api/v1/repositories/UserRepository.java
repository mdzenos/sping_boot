package com.vnpost.api.v1.repositories;

import org.springframework.stereotype.Repository;

import com.vnpost.api.v1.models.User;

import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {
}
