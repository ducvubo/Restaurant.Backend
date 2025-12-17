package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.branch.BranchDTO;
import com.restaurant.ddd.application.model.branch.CreateBranchRequest;
import com.restaurant.ddd.application.model.branch.UpdateBranchRequest;
import com.restaurant.ddd.domain.model.Branch;

public class BranchMapper {

    public static BranchDTO toDTO(Branch branch) {
        if (branch == null) return null;
        
        BranchDTO dto = new BranchDTO();
        dto.setId(branch.getId());
        dto.setCode(branch.getCode());
        dto.setName(branch.getName());
        dto.setEmail(branch.getEmail());
        dto.setPhone(branch.getPhone());
        dto.setAddress(branch.getAddress());
        dto.setOpeningTime(branch.getOpeningTime());
        dto.setClosingTime(branch.getClosingTime());
        dto.setStatus(branch.getStatus());
        dto.setCreatedBy(branch.getCreatedBy());
        dto.setUpdatedBy(branch.getUpdatedBy());
        dto.setCreatedDate(branch.getCreatedDate());
        dto.setUpdatedDate(branch.getUpdatedDate());
        return dto;
    }

    public static Branch toEntity(CreateBranchRequest request) {
        if (request == null) return null;
        
        Branch branch = new Branch();
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setEmail(request.getEmail());
        branch.setPhone(request.getPhone());
        branch.setAddress(request.getAddress());
        branch.setOpeningTime(request.getOpeningTime());
        branch.setClosingTime(request.getClosingTime());
        if (request.getStatus() != null) {
            branch.setStatus(request.getStatus());
        }
        return branch;
    }

    public static void updateEntity(Branch branch, UpdateBranchRequest request) {
        if (request == null || branch == null) return;
        
        if (request.getCode() != null) {
            branch.setCode(request.getCode());
        }
        if (request.getName() != null) {
            branch.setName(request.getName());
        }
        if (request.getEmail() != null) {
            branch.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            branch.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            branch.setAddress(request.getAddress());
        }
        if (request.getOpeningTime() != null) {
            branch.setOpeningTime(request.getOpeningTime());
        }
        if (request.getClosingTime() != null) {
            branch.setClosingTime(request.getClosingTime());
        }
        if (request.getStatus() != null) {
            branch.setStatus(request.getStatus());
        }
    }
}
