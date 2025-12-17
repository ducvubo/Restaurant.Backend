package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.branch.BranchDTO;
import com.restaurant.ddd.application.model.branch.BranchListRequest;
import com.restaurant.ddd.application.model.branch.BranchListResponse;
import com.restaurant.ddd.application.model.branch.CreateBranchRequest;
import com.restaurant.ddd.application.model.branch.UpdateBranchRequest;

import java.util.List;
import java.util.UUID;

public interface BranchAppService {
    BranchDTO create(CreateBranchRequest request, UUID userId);
    BranchDTO update(UpdateBranchRequest request, UUID userId);
    BranchDTO getById(UUID id);
    List<BranchDTO> getAll();
    List<BranchDTO> getAllActive();
    BranchListResponse getList(BranchListRequest request);
    BranchDTO activate(UUID id, UUID userId);
    BranchDTO deactivate(UUID id, UUID userId);
}
