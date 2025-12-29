package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.PurchaseOrderStatus;
import com.restaurant.ddd.domain.model.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PurchaseOrder
 */
public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder po);
    
    Optional<PurchaseOrder> findById(UUID id);
    
    Optional<PurchaseOrder> findByCode(String code);
    
    List<PurchaseOrder> findAll();
    
    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
    
    List<PurchaseOrder> findBySupplierId(UUID supplierId);
    
    List<PurchaseOrder> findByWarehouseId(UUID warehouseId);
    
    List<PurchaseOrder> findByRfqId(UUID rfqId);
    
    void deleteById(UUID id);
    
    boolean existsByCode(String code);
    
    /**
     * Generate next PO code
     */
    String generateNextCode();
    
    /**
     * Find all with filters and pagination
     */
    Page<PurchaseOrder> findAll(
        String keyword,
        UUID supplierId,
        UUID warehouseId,
        Integer status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    );
}
