package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.MaterialCategoryMapper;
import com.restaurant.ddd.application.model.material.CreateMaterialCategoryRequest;
import com.restaurant.ddd.application.model.material.MaterialCategoryDTO;
import com.restaurant.ddd.application.model.material.MaterialCategoryListRequest;
import com.restaurant.ddd.application.model.material.MaterialCategoryListResponse;
import com.restaurant.ddd.application.model.material.UpdateMaterialCategoryRequest;
import com.restaurant.ddd.application.service.MaterialCategoryService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.model.MaterialCategory;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.domain.respository.MaterialCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {

    private final MaterialCategoryRepository repository;

    @Override
    @Transactional
    public ResultMessage<MaterialCategoryDTO> create(CreateMaterialCategoryRequest request) {
        if (repository.findByCode(request.getCode()).isPresent()) {
            return new ResultMessage<>(ResultCode.ERROR, "Mã danh mục đã tồn tại", null);
        }

        MaterialCategory category = new MaterialCategory();
        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setStatus(DataStatus.ACTIVE);
        category.setCreatedDate(LocalDateTime.now());

        try {
            category.validate();
            category = repository.save(category);
            return new ResultMessage<>(ResultCode.SUCCESS, "Tạo danh mục thành công", MaterialCategoryMapper.toDTO(category));
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ResultMessage<MaterialCategoryDTO> update(UpdateMaterialCategoryRequest request) {
        var existing = repository.findById(request.getId());
        if (existing.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy danh mục", null);
        }

        MaterialCategory category = existing.get();
        if (!category.getCode().equals(request.getCode())) {
             if (repository.findByCode(request.getCode()).isPresent()) {
                return new ResultMessage<>(ResultCode.ERROR, "Mã danh mục đã tồn tại", null);
            }
        }

        category.setCode(request.getCode());
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setUpdatedDate(LocalDateTime.now());

        try {
            category.validate();
            category = repository.save(category);
            return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật danh mục thành công", MaterialCategoryMapper.toDTO(category));
        } catch (IllegalArgumentException e) {
            return new ResultMessage<>(ResultCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public ResultMessage<MaterialCategoryDTO> getById(UUID id) {
        return repository.findById(id)
                .map(c -> new ResultMessage<>(ResultCode.SUCCESS, "Lấy thông tin thành công", MaterialCategoryMapper.toDTO(c)))
                .orElse(new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy danh mục", null));
    }

    @Override
    public ResultMessage<MaterialCategoryListResponse> getList(MaterialCategoryListRequest request) {
        int page = request.getPage() != null && request.getPage() > 0 ? request.getPage() : 1;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 20;

        List<MaterialCategoryDTO> items = repository.find(page, size, request.getKeyword()).stream()
                .map(MaterialCategoryMapper::toDTO)
                .collect(Collectors.toList());
        long total = repository.count(request.getKeyword());

        MaterialCategoryListResponse response = new MaterialCategoryListResponse();
        response.setItems(items);
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);

        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách thành công", response);
    }

    @Override
    @Transactional
    public ResultMessage<String> delete(UUID id) {
        if (repository.findById(id).isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy danh mục", null);
        }
        repository.delete(id);
        return new ResultMessage<>(ResultCode.SUCCESS, "Xóa danh mục thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> activate(UUID id) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy danh mục", null);
        }
        MaterialCategory category = optional.get();
        category.setStatus(DataStatus.ACTIVE);
        repository.save(category);
        return new ResultMessage<>(ResultCode.SUCCESS, "Kích hoạt danh mục thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> deactivate(UUID id) {
        var optional = repository.findById(id);
        if (optional.isEmpty()) {
            return new ResultMessage<>(ResultCode.ERROR, "Không tìm thấy danh mục", null);
        }
        MaterialCategory category = optional.get();
        category.setStatus(DataStatus.INACTIVE);
        repository.save(category);
        return new ResultMessage<>(ResultCode.SUCCESS, "Ngưng hoạt động danh mục thành công", null);
    }
}
