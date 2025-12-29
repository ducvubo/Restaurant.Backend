package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.PurchaseRequisitionStatus;
import com.restaurant.ddd.domain.model.PurchaseRequisition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PurchaseRequisition
 */
public interface PurchaseRequisitionRepository {
    PurchaseRequisition save(PurchaseRequisition requisition);
    
    Optional<PurchaseRequisition> findById(UUID id);
    
    Optional<PurchaseRequisition> findByCode(String code);
    
    List<PurchaseRequisition> findAll();
    
    List<PurchaseRequisition> findByStatus(PurchaseRequisitionStatus status);
    
    List<PurchaseRequisition> findByWarehouseId(UUID warehouseId);
    
    void deleteById(UUID id);
    
    boolean existsByCode(String code);
    
    /**
     * Generate next requisition code
     */
    String generateNextCode();
    
    /**
     * Find all with filters and pagination
     */
    Page<PurchaseRequisition> findAll(
        String keyword,
        UUID warehouseId,
        Integer status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    );
}
