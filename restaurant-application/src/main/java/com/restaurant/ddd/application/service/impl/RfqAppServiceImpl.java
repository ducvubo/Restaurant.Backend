package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.RfqMapper;
import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.service.RfqAppService;
import com.restaurant.ddd.domain.enums.RfqStatus;
import com.restaurant.ddd.domain.model.PurchaseRequisition;
import com.restaurant.ddd.domain.model.PurchaseRequisitionItem;
import com.restaurant.ddd.domain.model.RequestForQuotation;
import com.restaurant.ddd.domain.model.RfqItem;
import com.restaurant.ddd.domain.respository.PurchaseRequisitionRepository;
import com.restaurant.ddd.domain.respository.RfqRepository;
import com.restaurant.ddd.infrastructure.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of RfqAppService
 */
@Service
@Slf4j
public class RfqAppServiceImpl implements RfqAppService {

    @Autowired
    private RfqRepository rfqRepository;

    @Autowired
    private PurchaseRequisitionRepository requisitionRepository;

    @Override
    @Transactional
    public RfqDTO create(RfqRequest request) {
        log.info("Creating new RFQ");
        
        String code = rfqRepository.generateNextCode();
        
        RequestForQuotation rfq = new RequestForQuotation();
        rfq.setId(UUID.randomUUID());
        rfq.setRfqCode(code);
        rfq.setRequisitionId(request.getRequisitionId());
        rfq.setSupplierId(request.getSupplierId());
        rfq.setValidUntil(request.getValidUntil());
        rfq.setPaymentTerms(request.getPaymentTerms());
        rfq.setDeliveryTerms(request.getDeliveryTerms());
        rfq.setNotes(request.getNotes());
        rfq.setStatus(RfqStatus.DRAFT);
        rfq.setCreatedBy(SecurityUtils.getCurrentUserId());
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        if (request.getItems() != null) {
            List<RfqItem> items = RfqMapper.toItemDomainList(request.getItems());
            rfq.setItems(items);
        }
        
        rfq.validate();
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RfqDTO createFromRequisition(UUID requisitionId, UUID supplierId) {
        log.info("Creating RFQ from requisition: {}", requisitionId);
        
        PurchaseRequisition pr = requisitionRepository.findById(requisitionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng: " + requisitionId));
        
        if (!pr.canCreateRfq()) {
            throw new RuntimeException("Không thể tạo RFQ từ yêu cầu mua hàng ở trạng thái: " + pr.getStatus().message());
        }
        
        String code = rfqRepository.generateNextCode();
        
        RequestForQuotation rfq = new RequestForQuotation();
        rfq.setId(UUID.randomUUID());
        rfq.setRfqCode(code);
        rfq.setRequisitionId(requisitionId);
        rfq.setRequisitionCode(pr.getRequisitionCode());
        rfq.setSupplierId(supplierId);
        rfq.setStatus(RfqStatus.DRAFT);
        rfq.setCreatedBy(SecurityUtils.getCurrentUserId());
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // Copy items from PR
        List<RfqItem> items = new ArrayList<>();
        if (pr.getItems() != null) {
            for (PurchaseRequisitionItem prItem : pr.getItems()) {
                RfqItem item = new RfqItem();
                item.setId(UUID.randomUUID());
                item.setMaterialId(prItem.getMaterialId());
                item.setMaterialCode(prItem.getMaterialCode());
                item.setMaterialName(prItem.getMaterialName());
                item.setQuantity(prItem.getQuantity());
                item.setUnitId(prItem.getUnitId());
                item.setUnitName(prItem.getUnitName());
                items.add(item);
            }
        }
        rfq.setItems(items);
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    public RfqDTO getById(UUID id) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        return RfqMapper.toDTO(rfq);
    }

    @Override
    public PageResponse<RfqDTO> getList(PurchaseListRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDir()) 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        int page = request.getPage() != null ? Math.max(0, request.getPage() - 1) : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<RequestForQuotation> result = rfqRepository.findAll(
                request.getKeyword(),
                request.getSupplierId(),
                request.getStatus(),
                request.getFromDate(),
                request.getToDate(),
                pageable
        );
        
        List<RfqDTO> dtos = result.getContent().stream()
                .map(RfqMapper::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(dtos, request.getPage(), size, result.getTotalElements());
    }

    @Override
    @Transactional
    public RfqDTO update(UUID id, RfqRequest request) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        
        rfq.setSupplierId(request.getSupplierId());
        rfq.setValidUntil(request.getValidUntil());
        rfq.setPaymentTerms(request.getPaymentTerms());
        rfq.setDeliveryTerms(request.getDeliveryTerms());
        rfq.setNotes(request.getNotes());
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        if (request.getItems() != null) {
            List<RfqItem> items = RfqMapper.toItemDomainList(request.getItems());
            rfq.setItems(items);
            rfq.calculateTotalAmount();
        }
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RfqDTO send(UUID id) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        
        rfq.send();
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RfqDTO receiveQuotation(UUID id, RfqRequest quotation) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        
        rfq.receiveQuotation();
        
        // Update with supplier prices
        if (quotation.getItems() != null) {
            List<RfqItem> items = RfqMapper.toItemDomainList(quotation.getItems());
            rfq.setItems(items);
            rfq.calculateTotalAmount();
        }
        
        rfq.setPaymentTerms(quotation.getPaymentTerms());
        rfq.setDeliveryTerms(quotation.getDeliveryTerms());
        rfq.setValidUntil(quotation.getValidUntil());
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RfqDTO accept(UUID id) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        
        rfq.accept();
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RfqDTO reject(UUID id) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        
        rfq.reject();
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public RfqDTO cancel(UUID id) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        
        rfq.cancel();
        rfq.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        RequestForQuotation saved = rfqRepository.save(rfq);
        return RfqMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        RequestForQuotation rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với id: " + id));
        
        if (rfq.getStatus() != RfqStatus.DRAFT) {
            throw new RuntimeException("Chỉ có thể xóa báo giá ở trạng thái Nháp");
        }
        
        rfqRepository.deleteById(id);
    }
}
