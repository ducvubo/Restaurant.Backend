package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialRepository {
    Material save(Material material);
    Optional<Material> findById(UUID id);
    List<Material> findAll();
    List<Material> findByStatus(DataStatus status);
    Optional<Material> findByCode(String code);
    List<Material> findByCategory(String category);
    
    /**
     * Find all materials with filters and pagination
     */
    Page<Material> findAll(
        String keyword,
        Integer status,
        String category,
        Pageable pageable
    );
}
