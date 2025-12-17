package com.restaurant.ddd.infrastructure.persistence.repository;

import com.restaurant.ddd.infrastructure.persistence.entity.StockOutBatchMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StockOutBatchMappingJpaRepository extends JpaRepository<StockOutBatchMappingJpaEntity, UUID> {
    List<StockOutBatchMappingJpaEntity> findByStockOutItemId(UUID stockOutItemId);
    
    @Query("SELECT bm FROM StockOutBatchMappingJpaEntity bm " +
           "JOIN StockOutItemJpaEntity item ON bm.stockOutItemId = item.id " +
           "WHERE item.stockOutTransactionId = :stockOutTransactionId")
    List<StockOutBatchMappingJpaEntity> findByStockOutTransactionId(@Param("stockOutTransactionId") UUID stockOutTransactionId);
    
    void deleteByStockOutItemId(UUID stockOutItemId);
}
