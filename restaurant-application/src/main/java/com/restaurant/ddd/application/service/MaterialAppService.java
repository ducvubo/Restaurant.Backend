package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.material.*;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

public interface MaterialAppService {
    ResultMessage<MaterialDTO> createMaterial(CreateMaterialRequest request);
    ResultMessage<MaterialDTO> updateMaterial(UpdateMaterialRequest request);
    ResultMessage<MaterialDTO> getMaterial(UUID id);
    ResultMessage<MaterialListResponse> getList(MaterialListRequest request);
    ResultMessage<String> activateMaterial(UUID id);
    ResultMessage<String> deactivateMaterial(UUID id);
}
