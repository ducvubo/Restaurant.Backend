package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.material.CreateMaterialCategoryRequest;
import com.restaurant.ddd.application.model.material.MaterialCategoryDTO;
import com.restaurant.ddd.application.model.material.MaterialCategoryListRequest;
import com.restaurant.ddd.application.model.material.MaterialCategoryListResponse;
import com.restaurant.ddd.application.model.material.UpdateMaterialCategoryRequest;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

public interface MaterialCategoryService {
    ResultMessage<MaterialCategoryDTO> create(CreateMaterialCategoryRequest request);
    ResultMessage<MaterialCategoryDTO> update(UpdateMaterialCategoryRequest request);
    ResultMessage<MaterialCategoryDTO> getById(UUID id);
    ResultMessage<MaterialCategoryListResponse> getList(MaterialCategoryListRequest request);
    ResultMessage<String> delete(UUID id);
    ResultMessage<String> activate(UUID id);
    ResultMessage<String> deactivate(UUID id);
}
