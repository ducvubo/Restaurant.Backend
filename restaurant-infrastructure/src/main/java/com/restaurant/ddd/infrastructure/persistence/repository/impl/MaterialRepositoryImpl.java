package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Material;
import com.restaurant.ddd.domain.respository.MaterialRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.MaterialJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.MaterialDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.MaterialJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MaterialRepositoryImpl implements MaterialRepository {

    private final MaterialJpaRepository materialJpaRepository;
    private final MaterialDataAccessMapper materialDataAccessMapper;

    @Override
    public Material save(Material material) {
        MaterialJpaEntity entity = materialDataAccessMapper.materialToMaterialJpaEntity(material);
        MaterialJpaEntity savedEntity = materialJpaRepository.save(entity);
        return materialDataAccessMapper.materialJpaEntityToMaterial(savedEntity);
    }

    @Override
    public Optional<Material> findById(UUID id) {
        return materialJpaRepository.findById(id)
                .map(materialDataAccessMapper::materialJpaEntityToMaterial);
    }

    @Override
    public List<Material> findAll() {
        return materialDataAccessMapper.materialJpaEntitiesToMaterials(materialJpaRepository.findAll());
    }

    @Override
    public List<Material> findByStatus(DataStatus status) {
        return materialDataAccessMapper.materialJpaEntitiesToMaterials(materialJpaRepository.findByStatus(status));
    }

    @Override
    public Optional<Material> findByCode(String code) {
        return materialJpaRepository.findByCode(code)
                .map(materialDataAccessMapper::materialJpaEntityToMaterial);
    }

    @Override
    public List<Material> findByCategory(String category) {
        return materialDataAccessMapper.materialJpaEntitiesToMaterials(materialJpaRepository.findByCategory(category));
    }
    
    @Override
    public org.springframework.data.domain.Page<Material> findAll(
            String keyword,
            Integer status,
            String category,
            org.springframework.data.domain.Pageable pageable) {
        
        return materialJpaRepository.findAll(
                com.restaurant.ddd.infrastructure.persistence.specification.MaterialSpecification.buildSpec(keyword, status, category),
                pageable
        ).map(materialDataAccessMapper::materialJpaEntityToMaterial);
    }
}
