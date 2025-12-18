package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.StockOutTransaction;
import com.restaurant.ddd.domain.respository.StockOutTransactionRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.StockOutTransactionJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.StockOutTransactionDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.StockOutTransactionJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.specification.StockTransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StockOutTransactionRepositoryImpl implements StockOutTransactionRepository {

    private final StockOutTransactionJpaRepository jpaRepository;
    private final StockOutTransactionDataAccessMapper mapper;

    @Override
    public Optional<StockOutTransaction> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomainModel);
    }

    @Override
    public List<StockOutTransaction> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public StockOutTransaction save(StockOutTransaction transaction) {
        StockOutTransactionJpaEntity entity = mapper.toEntity(transaction);
        StockOutTransactionJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomainModel(saved);
    }
    
    @Override
    public Page<StockOutTransaction> findAll(
            UUID warehouseId,
            UUID materialId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        
        return jpaRepository.findAll(
                StockTransactionSpecification.buildStockOutSpec(
                        warehouseId, materialId, startDate, endDate
                ),
                pageable
        ).map(mapper::toDomainModel);
    }
}
