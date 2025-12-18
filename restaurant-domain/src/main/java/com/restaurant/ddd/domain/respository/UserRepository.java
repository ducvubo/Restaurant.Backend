package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    Page<User> findAll(String keyword, Integer status, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

