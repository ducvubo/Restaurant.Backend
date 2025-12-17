package com.restaurant.ddd.domain.service;

import com.restaurant.ddd.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface AuthDomainService {
    Optional<User> findByUsername(String username);
    User saveUser(User user);
    boolean validatePassword(String rawPassword, String encodedPassword);
    String encodePassword(String rawPassword);
    Optional<User> authenticate(UUID jitToken, String clientId, UUID accountId);
}

