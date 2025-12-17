package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.UserPolicy;

import java.util.List;
import java.util.UUID;

public interface UserPolicyRepository extends BaseRepository<UserPolicy, UUID> {
    List<UserPolicy> findByUserId(UUID userId);
    List<UUID> findPolicyIdsByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
