package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.MaterialMapper;
import com.restaurant.ddd.application.model.material.*;
import com.restaurant.ddd.application.service.MaterialAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.model.Material;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.domain.respository.MaterialCategoryRepository;
import com.restaurant.ddd.domain.respository.MaterialRepository;
import com.restaurant.ddd.domain.respository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialAppServiceImpl implements MaterialAppService {

    private final MaterialRepository materialRepository;
    private final UnitRepository unitRepository;
    private final MaterialCategoryRepository materialCategoryRepository;

    @Override
    @Transactional
    public ResultMessage<MaterialDTO> createMaterial(CreateMaterialRequest request) {
        if (materialRepository.findByCode(request.getCode()).isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Mã nguyên vật liệu đã tồn tại", null);
        }

        if (request.getCategoryId() != null && materialCategoryRepository.findById(request.getCategoryId()).isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Danh mục không tồn tại", null);
        }

        Material material = new Material();
        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setCategory(request.getCategory());
        material.setCategoryId(request.getCategoryId());
        material.setUnitPrice(request.getUnitPrice());
        material.setMinStockLevel(request.getMinStockLevel());
        material.setMaxStockLevel(request.getMaxStockLevel());
        material.setDescription(request.getDescription());
        material.setStatus(DataStatus.ACTIVE);
        // material.setCreatedBy();
        material.setCreatedDate(LocalDateTime.now());

        try {
            material.validate();
            material = materialRepository.save(material);
            return new ResultMessage<>(ResultCode.SUCCESS, "Tạo nguyên vật liệu thành công", toDTO(material));
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResultMessage<MaterialDTO> updateMaterial(UpdateMaterialRequest request) {
        var existing = materialRepository.findById(request.getId());
        if (existing.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy nguyên vật liệu", null);
        }

        Material material = existing.get();
        if (!material.getCode().equals(request.getCode())) {
            if (materialRepository.findByCode(request.getCode()).isPresent()) {
                return new ResultMessage<>(ResultCode.ERROR, "Mã nguyên vật liệu đã tồn tại", null);
            }
        }

        if (request.getCategoryId() != null && !request.getCategoryId().equals(material.getCategoryId())) {
             if (materialCategoryRepository.findById(request.getCategoryId()).isEmpty()) {
                return new ResultMessage<>(ResultCode.ERROR, "Danh mục không tồn tại", null);
            }
        }

        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setCategory(request.getCategory());
        material.setCategoryId(request.getCategoryId());
        material.setUnitPrice(request.getUnitPrice());
        material.setMinStockLevel(request.getMinStockLevel());
        material.setMaxStockLevel(request.getMaxStockLevel());
        material.setDescription(request.getDescription());
        material.setUpdatedDate(LocalDateTime.now());

        try {
            material.validate();
            material = materialRepository.save(material);
            return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật nguyên vật liệu thành công", toDTO(material));
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResultMessage<MaterialDTO> getMaterial(UUID id) {
        return materialRepository.findById(id)
                .map(m -> new ResultMessage<>(ResultCode.SUCCESS, "Lấy thông tin nguyên vật liệu thành công", toDTO(m)))
                .orElse(new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy nguyên vật liệu", null));
    }

    @Override
    public ResultMessage<MaterialListResponse> getList(MaterialListRequest request) {
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
        org.springframework.data.domain.Page<Material> page = materialRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            request.getCategory(),
            pageable
        );
        
        // Map to DTOs
        List<MaterialDTO> dtos = page.getContent().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        
        MaterialListResponse response = new MaterialListResponse();
        response.setItems(dtos);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSafeSize());
        response.setTotalPages(page.getTotalPages());

        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách nguyên vật liệu thành công", response);
    }

    @Override
    @Transactional
    public ResultMessage<String> activateMaterial(UUID id) {
        var existing = materialRepository.findById(id);
        if (existing.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy nguyên vật liệu", null);
        }
        Material material = existing.get();
        material.activate();
        materialRepository.save(material);
        return new ResultMessage<>(ResultCode.SUCCESS, "Kích hoạt nguyên vật liệu thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> deactivateMaterial(UUID id) {
        var existing = materialRepository.findById(id);
        if (existing.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy nguyên vật liệu", null);
        }
        Material material = existing.get();
        material.deactivate();
        materialRepository.save(material);
        return new ResultMessage<>(ResultCode.SUCCESS, "Ngưng hoạt động nguyên vật liệu thành công", null);
    }
    
    private MaterialDTO toDTO(Material material) {
        MaterialDTO dto = MaterialMapper.toDTO(material);
        if (dto != null) {
             if (dto.getCategoryId() != null) {
                 materialCategoryRepository.findById(dto.getCategoryId()).ifPresent(cat -> dto.setCategoryName(cat.getName()));
             }
        }
        return dto;
    }
}
