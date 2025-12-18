package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.StockInTransaction;
import com.restaurant.ddd.domain.respository.StockInTransactionRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.StockInTransactionDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.StockInTransactionJpaRepository;
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
public class StockInTransactionRepositoryImpl implements StockInTransactionRepository {

    private final StockInTransactionJpaRepository jpaRepository;
    private final StockInTransactionDataAccessMapper mapper;

    @Override
    public StockInTransaction save(StockInTransaction transaction) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
    }

    @Override
    public Optional<StockInTransaction> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StockInTransaction> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<StockInTransaction> findAll(
            UUID warehouseId,
            UUID materialId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        
        return jpaRepository.findAll(
                StockTransactionSpecification.buildStockInSpec(
                        warehouseId, materialId, startDate, endDate
                ),
                pageable
        ).map(mapper::toDomain);
    }
}
