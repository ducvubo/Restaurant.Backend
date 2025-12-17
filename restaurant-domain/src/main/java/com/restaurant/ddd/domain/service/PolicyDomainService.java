package com.restaurant.ddd.domain.service;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Policy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyDomainService {
    Optional<Policy> findById(UUID id);
    List<Policy> findAll();
    List<Policy> findByStatus(DataStatus status);
    Policy save(Policy policy);
    void deleteById(UUID id);
}
