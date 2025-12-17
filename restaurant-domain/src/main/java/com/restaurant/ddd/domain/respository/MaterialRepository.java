package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Material;

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
}
