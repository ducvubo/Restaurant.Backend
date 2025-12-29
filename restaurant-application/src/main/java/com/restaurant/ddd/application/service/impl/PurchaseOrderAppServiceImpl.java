package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.PurchaseOrderMapper;
import com.restaurant.ddd.application.model.common.PageResponse;
import com.restaurant.ddd.application.model.purchasing.*;
import com.restaurant.ddd.application.service.PurchaseOrderAppService;
import com.restaurant.ddd.domain.enums.PurchaseOrderStatus;
import com.restaurant.ddd.domain.model.PurchaseOrder;
import com.restaurant.ddd.domain.model.PurchaseOrderItem;
import com.restaurant.ddd.domain.model.RequestForQuotation;
import com.restaurant.ddd.domain.model.RfqItem;
import com.restaurant.ddd.domain.respository.PurchaseOrderRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PurchaseOrderAppService
 */
@Service
@Slf4j
public class PurchaseOrderAppServiceImpl implements PurchaseOrderAppService {

    @Autowired
    private PurchaseOrderRepository poRepository;

    @Autowired
    private RfqRepository rfqRepository;

    @Override
    @Transactional
    public PurchaseOrderDTO create(PurchaseOrderRequest request) {
        log.info("Creating new Purchase Order");
        
        String code = poRepository.generateNextCode();
        
        PurchaseOrder po = new PurchaseOrder();
        po.setId(UUID.randomUUID());
        po.setPoCode(code);
        po.setRfqId(request.getRfqId());
        po.setSupplierId(request.getSupplierId());
        po.setWarehouseId(request.getWarehouseId());
        po.setOrderDate(LocalDateTime.now());
        po.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        po.setPaymentTerms(request.getPaymentTerms());
        po.setDeliveryTerms(request.getDeliveryTerms());
        po.setNotes(request.getNotes());
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setReceivedAmount(BigDecimal.ZERO);
        po.setCreatedBy(SecurityUtils.getCurrentUserId());
        po.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        if (request.getItems() != null) {
            List<PurchaseOrderItem> items = PurchaseOrderMapper.toItemDomainList(request.getItems());
            po.setItems(items);
            po.calculateTotalAmount();
        }
        
        po.validate();
        
        PurchaseOrder saved = poRepository.save(po);
        return PurchaseOrderMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO createFromRfq(UUID rfqId) {
        log.info("Creating PO from RFQ: {}", rfqId);
        
        RequestForQuotation rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá: " + rfqId));
        
        if (!rfq.canCreatePo()) {
            throw new RuntimeException("Không thể tạo PO từ báo giá ở trạng thái: " + rfq.getStatus().message());
        }
        
        String code = poRepository.generateNextCode();
        
        PurchaseOrder po = new PurchaseOrder();
        po.setId(UUID.randomUUID());
        po.setPoCode(code);
        po.setRfqId(rfqId);
        po.setRfqCode(rfq.getRfqCode());
        po.setSupplierId(rfq.getSupplierId());
        po.setSupplierName(rfq.getSupplierName());
        po.setOrderDate(LocalDateTime.now());
        po.setPaymentTerms(rfq.getPaymentTerms());
        po.setDeliveryTerms(rfq.getDeliveryTerms());
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setReceivedAmount(BigDecimal.ZERO);
        po.setCreatedBy(SecurityUtils.getCurrentUserId());
        po.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // Copy items from RFQ
        List<PurchaseOrderItem> items = new ArrayList<>();
        if (rfq.getItems() != null) {
            for (RfqItem rfqItem : rfq.getItems()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setId(UUID.randomUUID());
                item.setMaterialId(rfqItem.getMaterialId());
                item.setMaterialCode(rfqItem.getMaterialCode());
                item.setMaterialName(rfqItem.getMaterialName());
                item.setQuantity(rfqItem.getQuantity());
                item.setUnitId(rfqItem.getUnitId());
                item.setUnitName(rfqItem.getUnitName());
                item.setUnitPrice(rfqItem.getUnitPrice());
                item.setReceivedQuantity(BigDecimal.ZERO);
                item.calculateAmount();
                items.add(item);
            }
        }
        po.setItems(items);
        po.calculateTotalAmount();
        
        PurchaseOrder saved = poRepository.save(po);
        return PurchaseOrderMapper.toDTO(saved);
    }

    @Override
    public PurchaseOrderDTO getById(UUID id) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        return PurchaseOrderMapper.toDTO(po);
    }

    @Override
    public PageResponse<PurchaseOrderDTO> getList(PurchaseListRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDir()) 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        int page = request.getPage() != null ? Math.max(0, request.getPage() - 1) : 0;
        int size = request.getSize() != null ? request.getSize() : 10;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<PurchaseOrder> result = poRepository.findAll(
                request.getKeyword(),
                request.getSupplierId(),
                request.getWarehouseId(),
                request.getStatus(),
                request.getFromDate(),
                request.getToDate(),
                pageable
        );
        
        List<PurchaseOrderDTO> dtos = result.getContent().stream()
                .map(PurchaseOrderMapper::toDTO)
                .collect(Collectors.toList());
        
        return PageResponse.of(dtos, request.getPage(), size, result.getTotalElements());
    }

    @Override
    @Transactional
    public PurchaseOrderDTO update(UUID id, PurchaseOrderRequest request) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        
        if (!po.canEdit()) {
            throw new RuntimeException("Không thể sửa đơn hàng ở trạng thái: " + po.getStatus().message());
        }
        
        po.setSupplierId(request.getSupplierId());
        po.setWarehouseId(request.getWarehouseId());
        po.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        po.setPaymentTerms(request.getPaymentTerms());
        po.setDeliveryTerms(request.getDeliveryTerms());
        po.setNotes(request.getNotes());
        po.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        if (request.getItems() != null) {
            List<PurchaseOrderItem> items = PurchaseOrderMapper.toItemDomainList(request.getItems());
            po.setItems(items);
            po.calculateTotalAmount();
        }
        
        po.validate();
        
        PurchaseOrder saved = poRepository.save(po);
        return PurchaseOrderMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO confirm(UUID id) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        
        po.confirm();
        po.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseOrder saved = poRepository.save(po);
        return PurchaseOrderMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO receiveGoods(UUID id, ReceiveGoodsRequest request) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        
        if (!po.canReceiveGoods()) {
            throw new RuntimeException("Không thể nhận hàng cho đơn hàng ở trạng thái: " + po.getStatus().message());
        }
        
        BigDecimal totalReceivedValue = BigDecimal.ZERO;
        
        // Update received quantities
        if (request.getItems() != null) {
            for (ReceiveGoodsRequest.ReceiveGoodsItemRequest itemReq : request.getItems()) {
                for (PurchaseOrderItem poItem : po.getItems()) {
                    if (poItem.getId().equals(itemReq.getPoItemId())) {
                        poItem.receiveQuantity(itemReq.getReceivedQuantity());
                        BigDecimal itemValue = itemReq.getReceivedQuantity().multiply(poItem.getUnitPrice());
                        totalReceivedValue = totalReceivedValue.add(itemValue);
                        break;
                    }
                }
            }
        }
        
        // Update PO status
        po.receiveGoods(totalReceivedValue);
        po.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        // TODO: Create StockIn transaction
        
        PurchaseOrder saved = poRepository.save(po);
        return PurchaseOrderMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO cancel(UUID id) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        
        po.cancel();
        po.setUpdatedBy(SecurityUtils.getCurrentUserId());
        
        PurchaseOrder saved = poRepository.save(po);
        return PurchaseOrderMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + id));
        
        if (po.getStatus() != PurchaseOrderStatus.DRAFT) {
            throw new RuntimeException("Chỉ có thể xóa đơn hàng ở trạng thái Nháp");
        }
        
        poRepository.deleteById(id);
    }
}
