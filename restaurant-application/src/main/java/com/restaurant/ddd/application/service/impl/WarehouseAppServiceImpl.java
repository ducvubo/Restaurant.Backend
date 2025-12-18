package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.WarehouseMapper;
import com.restaurant.ddd.application.model.warehouse.*;
import com.restaurant.ddd.application.service.WarehouseAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.enums.WarehouseType;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.domain.model.Warehouse;
import com.restaurant.ddd.domain.respository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseAppServiceImpl implements WarehouseAppService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public ResultMessage<WarehouseDTO> createWarehouse(CreateWarehouseRequest request) {
        // Validate code uniqueness
        if (warehouseRepository.findByCode(request.getCode()).isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Mã kho đã tồn tại", null);
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setBranchId(request.getBranchId());
        warehouse.setAddress(request.getAddress());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setManagerId(request.getManagerId());
        
        if (request.getWarehouseType() != null) {
            warehouse.setWarehouseType(WarehouseType.fromCode(request.getWarehouseType()));
        }

        warehouse.setStatus(DataStatus.ACTIVE);
        // warehouse.setCreatedBy(); // TODO: get from context
        warehouse.setCreatedDate(LocalDateTime.now());

        try {
            warehouse.validate();
            warehouse = warehouseRepository.save(warehouse);
            return new ResultMessage<>(ResultCode.SUCCESS, "Tạo kho thành công", WarehouseMapper.toDTO(warehouse));
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResultMessage<WarehouseDTO> updateWarehouse(UpdateWarehouseRequest request) {
        var existing = warehouseRepository.findById(request.getId());
        if (existing.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy kho", null);
        }

        Warehouse warehouse = existing.get();
        // Check code uniqueness if changed
        if (!warehouse.getCode().equals(request.getCode())) {
            if (warehouseRepository.findByCode(request.getCode()).isPresent()) {
                return new ResultMessage<>(ResultCode.ERROR, "Mã kho đã tồn tại", null);
            }
        }

        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setBranchId(request.getBranchId());
        warehouse.setAddress(request.getAddress());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setManagerId(request.getManagerId());
        
        if (request.getWarehouseType() != null) {
            warehouse.setWarehouseType(WarehouseType.fromCode(request.getWarehouseType()));
        }

        warehouse.setUpdatedDate(LocalDateTime.now());
        // warehouse.setUpdatedBy(); 

        try {
            warehouse.validate();
            warehouse = warehouseRepository.save(warehouse);
            return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật kho thành công", WarehouseMapper.toDTO(warehouse));
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResultMessage<WarehouseDTO> getWarehouse(UUID id) {
        return warehouseRepository.findById(id)
                .map(w -> new ResultMessage<>(ResultCode.SUCCESS, "Lấy thông tin kho thành công", WarehouseMapper.toDTO(w)))
                .orElse(new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy kho", null));
    }

    @Override
    public ResultMessage<WarehouseListResponse> getList(WarehouseListRequest request) {
        // Build Pageable with sorting
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "name";
        org.springframework.data.domain.Sort.Direction direction = 
            "ASC".equalsIgnoreCase(request.getSafeSortDirection()) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPageZeroBased(),
            request.getSafeSize(),
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
        
        // Query with pagination
        org.springframework.data.domain.Page<Warehouse> page = warehouseRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            request.getBranchId(),
            request.getWarehouseType(),
            pageable
        );
        
        // Map to DTOs
        List<WarehouseDTO> dtos = page.getContent().stream()
            .map(WarehouseMapper::toDTO)
            .collect(Collectors.toList());
        
        WarehouseListResponse response = new WarehouseListResponse();
        response.setItems(dtos);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSafeSize());
        response.setTotalPages(page.getTotalPages());

        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách kho thành công", response);
    }

    @Override
    @Transactional
    public ResultMessage<String> activateWarehouse(UUID id) {
        var existing = warehouseRepository.findById(id);
        if (existing.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy kho", null);
        }
        Warehouse warehouse = existing.get();
        warehouse.activate();
        warehouseRepository.save(warehouse);
        return new ResultMessage<>(ResultCode.SUCCESS, "Kích hoạt kho thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> deactivateWarehouse(UUID id) {
        var existing = warehouseRepository.findById(id);
        if (existing.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy kho", null);
        }
        Warehouse warehouse = existing.get();
        warehouse.deactivate();
        warehouseRepository.save(warehouse);
        return new ResultMessage<>(ResultCode.SUCCESS, "Ngưng hoạt động kho thành công", null);
    }
}
