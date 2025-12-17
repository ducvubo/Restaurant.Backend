package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.StockOutBatchMapping;
import com.restaurant.ddd.domain.respository.StockOutBatchMappingRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.StockOutBatchMappingJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.StockOutBatchMappingDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.StockOutBatchMappingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockOutBatchMappingRepositoryImpl implements StockOutBatchMappingRepository {

    private final StockOutBatchMappingJpaRepository jpaRepository;
    private final StockOutBatchMappingDataAccessMapper mapper;

    @Override
    public StockOutBatchMapping save(StockOutBatchMapping mapping) {
        StockOutBatchMappingJpaEntity entity = mapper.toEntity(mapping);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<StockOutBatchMapping> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StockOutBatchMapping> findByStockOutItemId(UUID stockOutItemId) {
        return mapper.toDomainList(jpaRepository.findByStockOutItemId(stockOutItemId));
    }

    @Override
    public List<StockOutBatchMapping> findByStockOutTransactionId(UUID stockOutTransactionId) {
        return mapper.toDomainList(jpaRepository.findByStockOutTransactionId(stockOutTransactionId));
    }

    @Override
    public void delete(StockOutBatchMapping mapping) {
        jpaRepository.deleteById(mapping.getId());
    }

    @Override
    public void deleteByStockOutItemId(UUID stockOutItemId) {
        jpaRepository.deleteByStockOutItemId(stockOutItemId);
    }
}
