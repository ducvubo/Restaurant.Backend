package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.AdjustmentBatchMapping;
import com.restaurant.ddd.domain.respository.AdjustmentBatchMappingRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.AdjustmentBatchMappingDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.AdjustmentBatchMappingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AdjustmentBatchMappingRepositoryImpl implements AdjustmentBatchMappingRepository {

    private final AdjustmentBatchMappingJpaRepository jpaRepository;
    private final AdjustmentBatchMappingDataAccessMapper mapper;

    @Override
    public AdjustmentBatchMapping save(AdjustmentBatchMapping mapping) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(mapping)));
    }

    @Override
    public List<AdjustmentBatchMapping> findByAdjustmentItemId(UUID itemId) {
        return jpaRepository.findByAdjustmentItemId(itemId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(AdjustmentBatchMapping mapping) {
        jpaRepository.deleteById(mapping.getId());
    }
}
