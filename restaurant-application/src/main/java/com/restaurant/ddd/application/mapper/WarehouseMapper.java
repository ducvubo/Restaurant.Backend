package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.warehouse.WarehouseDTO;
import com.restaurant.ddd.domain.model.Warehouse;

public class WarehouseMapper {

    public static WarehouseDTO toDTO(Warehouse warehouse) {
        if (warehouse == null) return null;

        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setCode(warehouse.getCode());
        dto.setName(warehouse.getName());
        dto.setBranchId(warehouse.getBranchId());
        dto.setAddress(warehouse.getAddress());
        dto.setCapacity(warehouse.getCapacity());
        dto.setManagerId(warehouse.getManagerId());
        dto.setWarehouseType(warehouse.getWarehouseType() != null ? warehouse.getWarehouseType().code() : null);
        dto.setWarehouseTypeName(warehouse.getWarehouseType() != null ? warehouse.getWarehouseType().message() : null);
        dto.setStatus(warehouse.getStatus() != null ? warehouse.getStatus().code() : null);
        dto.setCreatedBy(warehouse.getCreatedBy());
        dto.setUpdatedBy(warehouse.getUpdatedBy());
        dto.setCreatedDate(warehouse.getCreatedDate());
        dto.setUpdatedDate(warehouse.getUpdatedDate());
        
        return dto;
    }
}
