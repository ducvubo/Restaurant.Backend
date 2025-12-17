package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.StockInItem;
import com.restaurant.ddd.domain.respository.StockInItemRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.StockInItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.StockInItemDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.StockInItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StockInItemRepositoryImpl implements StockInItemRepository {

    private final StockInItemJpaRepository jpaRepository;
    private final StockInItemDataAccessMapper mapper;

    @Override
    public StockInItem save(StockInItem item) {
        StockInItemJpaEntity entity = mapper.toEntity(item);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<StockInItem> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StockInItem> findByStockInTransactionId(UUID stockInTransactionId) {
        return mapper.toDomainList(jpaRepository.findByStockInTransactionId(stockInTransactionId));
    }

    @Override
    public void deleteByStockInTransactionId(UUID stockInTransactionId) {
        jpaRepository.deleteByStockInTransactionId(stockInTransactionId);
    }
}
