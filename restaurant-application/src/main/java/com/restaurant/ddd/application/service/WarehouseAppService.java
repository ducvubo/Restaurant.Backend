package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.warehouse.*;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

public interface WarehouseAppService {
    ResultMessage<WarehouseDTO> createWarehouse(CreateWarehouseRequest request);
    ResultMessage<WarehouseDTO> updateWarehouse(UpdateWarehouseRequest request);
    ResultMessage<WarehouseDTO> getWarehouse(UUID id);
    ResultMessage<WarehouseListResponse> getList(WarehouseListRequest request);
    ResultMessage<String> activateWarehouse(UUID id);
    ResultMessage<String> deactivateWarehouse(UUID id);
}
