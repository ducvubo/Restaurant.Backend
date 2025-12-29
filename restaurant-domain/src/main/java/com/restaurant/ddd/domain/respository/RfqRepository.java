package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.enums.RfqStatus;
import com.restaurant.ddd.domain.model.RequestForQuotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for RequestForQuotation (RFQ)
 */
public interface RfqRepository {
    RequestForQuotation save(RequestForQuotation rfq);
    
    Optional<RequestForQuotation> findById(UUID id);
    
    Optional<RequestForQuotation> findByCode(String code);
    
    List<RequestForQuotation> findAll();
    
    List<RequestForQuotation> findByStatus(RfqStatus status);
    
    List<RequestForQuotation> findBySupplierId(UUID supplierId);
    
    List<RequestForQuotation> findByRequisitionId(UUID requisitionId);
    
    void deleteById(UUID id);
    
    boolean existsByCode(String code);
    
    /**
     * Generate next RFQ code
     */
    String generateNextCode();
    
    /**
     * Find expired RFQs
     */
    List<RequestForQuotation> findExpired(LocalDateTime now);
    
    /**
     * Find all with filters and pagination
     */
    Page<RequestForQuotation> findAll(
        String keyword,
        UUID supplierId,
        Integer status,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        Pageable pageable
    );
}
