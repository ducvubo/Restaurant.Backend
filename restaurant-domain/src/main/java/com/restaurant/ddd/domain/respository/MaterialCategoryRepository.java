package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.MaterialCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaterialCategoryRepository {
    MaterialCategory save(MaterialCategory category);
    Optional<MaterialCategory> findById(UUID id);
    Optional<MaterialCategory> findByCode(String code);
    List<MaterialCategory> findAll();
    List<MaterialCategory> find(int page, int size, String keyword);
    long count(String keyword);
    void delete(UUID id);
}
