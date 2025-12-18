package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends BaseRepository<Branch, UUID> {
    Optional<Branch> findById(UUID id);
    List<Branch> findAll();
    Page<Branch> findAll(String keyword, Integer status, Pageable pageable);
    List<Branch> findByStatus(DataStatus status);
    Optional<Branch> findByCode(String code);
    Optional<Branch> findByEmail(String email);
    Optional<Branch> findByPhone(String phone);
    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
