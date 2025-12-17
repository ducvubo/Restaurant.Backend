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

        if (request.getUnitId() != null && unitRepository.findById(request.getUnitId()).isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Đơn vị tính không tồn tại", null);
        }

        if (request.getCategoryId() != null && materialCategoryRepository.findById(request.getCategoryId()).isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Danh mục không tồn tại", null);
        }

        Material material = new Material();
        material.setCode(request.getCode());
        material.setName(request.getName());
        material.setCategory(request.getCategory());
        material.setCategoryId(request.getCategoryId());
        material.setUnitId(request.getUnitId());
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

        if (request.getUnitId() != null && !request.getUnitId().equals(material.getUnitId())) {
             if (unitRepository.findById(request.getUnitId()).isEmpty()) {
                return new ResultMessage<>(ResultCode.ERROR, "Đơn vị tính không tồn tại", null);
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
        material.setUnitId(request.getUnitId());
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
        List<Material> all = materialRepository.findAll();
        
        List<MaterialDTO> dtos = all.stream()
            .filter(m -> request.getKeyword() == null || m.getName().toLowerCase().contains(request.getKeyword().toLowerCase()) || m.getCode().toLowerCase().contains(request.getKeyword().toLowerCase()))
            .filter(m -> request.getStatus() == null || (m.getStatus() != null && m.getStatus().code().equals(request.getStatus())))
            .filter(m -> request.getCategory() == null || (m.getCategory() != null && m.getCategory().equals(request.getCategory())))
            // TODO: Filter by categoryId if needed in request
            .map(this::toDTO)
            .collect(Collectors.toList());

        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 10;
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, dtos.size());
        
        List<MaterialDTO> pagedDtos;
        if (fromIndex >= dtos.size()) {
            pagedDtos = List.of();
        } else {
            pagedDtos = dtos.subList(fromIndex, toIndex);
        }

        MaterialListResponse response = new MaterialListResponse();
        response.setItems(pagedDtos);
        response.setTotal(dtos.size());
        response.setPage(page);
        response.setSize(size);
        response.setTotalPages((int) Math.ceil((double) dtos.size() / size));

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
             if (dto.getUnitId() != null) {
                unitRepository.findById(dto.getUnitId()).ifPresent(unit -> dto.setUnitName(unit.getName()));
             }
             if (dto.getCategoryId() != null) {
                 materialCategoryRepository.findById(dto.getCategoryId()).ifPresent(cat -> dto.setCategoryName(cat.getName()));
             }
        }
        return dto;
    }
}
