package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.InventoryCount;
import com.restaurant.ddd.domain.respository.InventoryCountRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.InventoryCountDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.InventoryCountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryCountRepositoryImpl implements InventoryCountRepository {
    
    private final InventoryCountJpaRepository jpaRepository;
    private final InventoryCountDataAccessMapper mapper;
    
    @Override
    public InventoryCount save(InventoryCount inventoryCount) {
        InventoryCountJpaEntity entity = mapper.toEntity(inventoryCount);
        InventoryCountJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<InventoryCount> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<InventoryCount> findByCountCode(String countCode) {
        return jpaRepository.findByCountCode(countCode)
                .map(mapper::toDomain);
    }
    
    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
