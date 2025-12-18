package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository extends BaseRepository<Policy, UUID> {
    Optional<Policy> findById(UUID id);
    List<Policy> findAll();
    Page<Policy> findAll(String keyword, Integer status, Pageable pageable);
    List<Policy> findByStatus(DataStatus status);
}
