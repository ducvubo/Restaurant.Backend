package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.StockOutItem;
import com.restaurant.ddd.domain.respository.StockOutItemRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.StockOutItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.StockOutItemDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.StockOutItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockOutItemRepositoryImpl implements StockOutItemRepository {

    private final StockOutItemJpaRepository jpaRepository;
    private final StockOutItemDataAccessMapper mapper;

    @Override
    public StockOutItem save(StockOutItem item) {
        StockOutItemJpaEntity entity = mapper.toEntity(item);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<StockOutItem> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StockOutItem> findByStockOutTransactionId(UUID stockOutTransactionId) {
        return mapper.toDomainList(jpaRepository.findByStockOutTransactionId(stockOutTransactionId));
    }

    @Override
    public void deleteByStockOutTransactionId(UUID stockOutTransactionId) {
        jpaRepository.deleteByStockOutTransactionId(stockOutTransactionId);
    }
}
