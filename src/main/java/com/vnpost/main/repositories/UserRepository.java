package com.vnpost.main.repositories;

import com.vnpost.main.models.User;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, UUID> {
}
