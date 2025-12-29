package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.RfqStatus;
import com.restaurant.ddd.domain.model.RequestForQuotation;
import com.restaurant.ddd.domain.model.RfqItem;
import com.restaurant.ddd.domain.respository.RfqRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.RfqItemJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.entity.RfqJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.RfqDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.mapper.RfqItemJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.mapper.RfqJpaRepository;
import com.restaurant.ddd.infrastructure.persistence.specification.RfqSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RfqRepository
 */
@Repository
@Slf4j
public class RfqRepositoryImpl implements RfqRepository {

    @Autowired
    private RfqJpaRepository rfqJpaRepository;

    @Autowired
    private RfqItemJpaRepository rfqItemJpaRepository;

    @Autowired
    private RfqDataAccessMapper mapper;

    @Override
    @Transactional
    public RequestForQuotation save(RequestForQuotation rfq) {
        log.debug("Saving RFQ: {}", rfq.getRfqCode());
        
        // Save header
        RfqJpaEntity entity = mapper.toEntity(rfq);
        RfqJpaEntity savedEntity = rfqJpaRepository.save(entity);
        
        // Delete existing items and save new ones
        rfqItemJpaRepository.deleteByRfqId(savedEntity.getId());
        
        if (rfq.getItems() != null && !rfq.getItems().isEmpty()) {
            for (RfqItem item : rfq.getItems()) {
                item.setRfqId(savedEntity.getId());
                if (item.getId() == null) {
                    item.setId(UUID.randomUUID());
                }
            }
            List<RfqItemJpaEntity> itemEntities = mapper.itemsToEntity(rfq.getItems());
            rfqItemJpaRepository.saveAll(itemEntities);
        }
        
        // Return with items
        RequestForQuotation result = mapper.toDomain(savedEntity);
        result.setItems(rfq.getItems());
        return result;
    }

    @Override
    public Optional<RequestForQuotation> findById(UUID id) {
        log.debug("Finding RFQ by id: {}", id);
        return rfqJpaRepository.findById(id)
                .map(entity -> {
                    RequestForQuotation rfq = mapper.toDomain(entity);
                    List<RfqItemJpaEntity> items = rfqItemJpaRepository.findByRfqId(id);
                    rfq.setItems(mapper.itemsToDomain(items));
                    return rfq;
                });
    }

    @Override
    public Optional<RequestForQuotation> findByCode(String code) {
        log.debug("Finding RFQ by code: {}", code);
        return rfqJpaRepository.findByRfqCode(code)
                .map(entity -> {
                    RequestForQuotation rfq = mapper.toDomain(entity);
                    List<RfqItemJpaEntity> items = rfqItemJpaRepository.findByRfqId(entity.getId());
                    rfq.setItems(mapper.itemsToDomain(items));
                    return rfq;
                });
    }

    @Override
    public List<RequestForQuotation> findAll() {
        log.debug("Finding all RFQs");
        return rfqJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestForQuotation> findByStatus(RfqStatus status) {
        log.debug("Finding RFQs by status: {}", status);
        DataStatus dataStatus = DataStatus.fromCode(status.code());
        return rfqJpaRepository.findByStatus(dataStatus).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestForQuotation> findBySupplierId(UUID supplierId) {
        log.debug("Finding RFQs by supplier: {}", supplierId);
        return rfqJpaRepository.findBySupplierId(supplierId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestForQuotation> findByRequisitionId(UUID requisitionId) {
        log.debug("Finding RFQs by requisition: {}", requisitionId);
        return rfqJpaRepository.findByRequisitionId(requisitionId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Deleting RFQ by id: {}", id);
        rfqItemJpaRepository.deleteByRfqId(id);
        rfqJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return rfqJpaRepository.existsByRfqCode(code);
    }

    @Override
    public String generateNextCode() {
        String prefix = "RFQ-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long count = rfqJpaRepository.count() + 1;
        return prefix + String.format("%03d", count);
    }

    @Override
    public List<RequestForQuotation> findExpired(LocalDateTime now) {
        return rfqJpaRepository.findByValidUntilBeforeAndStatusNot(now, DataStatus.INACTIVE).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RequestForQuotation> findAll(
            String keyword,
            UUID supplierId,
            Integer status,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable) {
        
        return rfqJpaRepository.findAll(
                RfqSpecification.buildSpec(keyword, supplierId, status, fromDate, toDate),
                pageable
        ).map(mapper::toDomain);
    }
}
