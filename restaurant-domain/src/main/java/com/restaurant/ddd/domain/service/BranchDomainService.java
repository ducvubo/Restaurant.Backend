package com.restaurant.ddd.domain.service;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Branch;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchDomainService {
    Optional<Branch> findById(UUID id);
    List<Branch> findAll();
    List<Branch> findByStatus(DataStatus status);
    Branch save(Branch branch);
    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
