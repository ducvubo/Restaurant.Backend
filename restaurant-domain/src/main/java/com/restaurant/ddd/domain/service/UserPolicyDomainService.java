package com.restaurant.ddd.domain.service;

import com.restaurant.ddd.domain.model.UserPolicy;

import java.util.List;
import java.util.UUID;

public interface UserPolicyDomainService {
    List<UserPolicy> findByUserId(UUID userId);
    List<UUID> findPolicyIdsByUserId(UUID userId);
    UserPolicy save(UserPolicy userPolicy);
    void deleteByUserId(UUID userId);
}
