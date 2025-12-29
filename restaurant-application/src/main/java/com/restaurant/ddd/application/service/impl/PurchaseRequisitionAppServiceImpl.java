package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.PurchaseRequisitionMapper;
import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.service.PurchaseRequisitionAppService;
import com.restaurant.ddd.domain.enums.PurchasePriority;
import com.restaurant.ddd.domain.enums.PurchaseRequisitionStatus;
import com.restaurant.ddd.domain.model.PurchaseRequisition;
import com.restaurant.ddd.domain.model.PurchaseRequisitionItem;
import com.restaurant.ddd.domain.respository.PurchaseRequisitionRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PurchaseRequisitionAppService
 */
@Service
@Slf4j
public class PurchaseRequisitionAppServiceImpl implements PurchaseRequisitionAppService {

    @Autowired
    private PurchaseRequisitionRepository requisitionRepository;

    @Override
    @Transactional
    public PurchaseRequisitionDTO create(PurchaseRequisitionRequest request) {
        log.info("Creating new purchase requisition");
        
        // Generate code
        String code = requisitionRepository.generateNextCode();
        
        // Create domain model
        PurchaseRequisition pr = new PurchaseRequisition();
        pr.setId(UUID.randomUUID());
        pr.setRequisitionCode(code);
        pr.setWarehouseId(request.getWarehouseId());
        pr.setRequestedBy(SecurityUtils.getCurrentUserId());
        pr.setRequestDate(LocalDateTime.now());
        pr.setRequiredDate(request.getRequiredDate());
        pr.setPriority(PurchasePriority.fromCode(request.getPriority()));
        pr.setNotes(request.getNotes());
        pr.setStatus(PurchaseRequisitionStatus.DRAFT);
        pr.setCreatedBy(SecurityUtils.getCurrentUserId());
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // Map items
        if (request.getItems() != null) {
            List<PurchaseRequisitionItem> items = PurchaseRequisitionMapper.toItemDomainList(request.getItems());
            pr.setItems(items);
        }
        
        // Validate
        pr.validate();
        
        // Save
        PurchaseRequisition saved = requisitionRepository.save(pr);
        
        return PurchaseRequisitionMapper.toDTO(saved);
    }

    @Override
    public PurchaseRequisitionDTO getById(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        return PurchaseRequisitionMapper.toDTO(pr);
    }

    @Override
    public PageResponse<PurchaseRequisitionDTO> getList(PurchaseListRequest request) {
        // Build Pageable
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDir()) 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        int page = request.getPage() != null ? Math.max(0, request.getPage() - 1) : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Query
        Page<PurchaseRequisition> result = requisitionRepository.findAll(
                request.getKeyword(),
                request.getWarehouseId(),
                request.getStatus(),
                request.getFromDate(),
                request.getToDate(),
                pageable
        );
        
        // Map to DTOs
        List<PurchaseRequisitionDTO> dtos = result.getContent().stream()
                .map(PurchaseRequisitionMapper::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(dtos, request.getPage(), size, result.getTotalElements());
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO update(UUID id, PurchaseRequisitionRequest request) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        // Check if can edit
        if (!pr.canEdit()) {
            throw new RuntimeException("Không thể sửa yêu cầu mua hàng ở trạng thái: " + pr.getStatus().message());
        }
        
        // Update fields
        pr.setWarehouseId(request.getWarehouseId());
        pr.setRequiredDate(request.getRequiredDate());
        pr.setPriority(PurchasePriority.fromCode(request.getPriority()));
        pr.setNotes(request.getNotes());
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // Update items
        if (request.getItems() != null) {
            List<PurchaseRequisitionItem> items = PurchaseRequisitionMapper.toItemDomainList(request.getItems());
            pr.setItems(items);
        }
        
        // Validate
        pr.validate();
        
        // Save
        PurchaseRequisition saved = requisitionRepository.save(pr);
        
        return PurchaseRequisitionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO submit(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.submit();
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO approve(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.approve(SecurityUtils.getCurrentUserId());
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO reject(UUID id, String reason) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.reject(SecurityUtils.getCurrentUserId(), reason);
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseRequisitionDTO cancel(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        pr.cancel();
        pr.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseRequisition saved = requisitionRepository.save(pr);
        return PurchaseRequisitionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PurchaseRequisition pr = requisitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu mua hàng với id: " + id));
        
        if (pr.getStatus() != PurchaseRequisitionStatus.DRAFT) {
            throw new RuntimeException("Chỉ có thể xóa yêu cầu mua hàng ở trạng thái Nháp");
        }
        
        requisitionRepository.deleteById(id);
    }
}
