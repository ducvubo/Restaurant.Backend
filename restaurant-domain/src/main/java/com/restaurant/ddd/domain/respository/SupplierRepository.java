package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Supplier
 */
public interface SupplierRepository {
    Supplier save(Supplier supplier);
    Optional<Supplier> findById(UUID id);
    Optional<Supplier> findByCode(String code);
    List<Supplier> findAll();
    List<Supplier> findByStatus(DataStatus status);
    void deleteById(UUID id);
    boolean existsByCode(String code);
    boolean existsByEmail(String email);
    
    /**
     * Find all suppliers with filters and pagination
     */
    Page<Supplier> findAll(
        String keyword,
        Integer status,
        Pageable pageable
    );
}
