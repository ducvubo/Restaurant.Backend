package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.AdjustmentItem;
import com.restaurant.ddd.domain.respository.AdjustmentItemRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.AdjustmentItemDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.AdjustmentItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AdjustmentItemRepositoryImpl implements AdjustmentItemRepository {

    private final AdjustmentItemJpaRepository jpaRepository;
    private final AdjustmentItemDataAccessMapper mapper;

    @Override
    public AdjustmentItem save(AdjustmentItem item) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(item)));
    }

    @Override
    public Optional<AdjustmentItem> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AdjustmentItem> findByAdjustmentTransactionId(UUID transactionId) {
        return jpaRepository.findByAdjustmentTransactionId(transactionId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
