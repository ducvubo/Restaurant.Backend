package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.InventoryCountItem;
import com.restaurant.ddd.domain.respository.InventoryCountItemRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.InventoryCountItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.InventoryCountItemDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.InventoryCountItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InventoryCountItemRepositoryImpl implements InventoryCountItemRepository {
    
    private final InventoryCountItemJpaRepository jpaRepository;
    private final InventoryCountItemDataAccessMapper mapper;
    
    @Override
    public InventoryCountItem save(InventoryCountItem item) {
        InventoryCountItemJpaEntity entity = mapper.toEntity(item);
        InventoryCountItemJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<InventoryCountItem> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<InventoryCountItem> findByInventoryCountId(UUID inventoryCountId) {
        return jpaRepository.findByInventoryCountId(inventoryCountId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteByInventoryCountId(UUID inventoryCountId) {
        jpaRepository.deleteByInventoryCountId(inventoryCountId);
    }
}
