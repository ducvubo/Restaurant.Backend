package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.user.CreatePolicyRequest;
import com.restaurant.ddd.application.model.user.PolicyDTO;
import com.restaurant.ddd.application.model.user.PolicyListRequest;
import com.restaurant.ddd.application.model.user.PolicyListResponse;
import com.restaurant.ddd.application.model.user.UpdatePolicyRequest;

import java.util.List;
import java.util.UUID;

public interface PolicyAppService {
    PolicyDTO create(CreatePolicyRequest request, UUID userId);
    PolicyDTO update(UpdatePolicyRequest request, UUID userId);
    PolicyDTO getById(UUID id);
    void delete(UUID id);
    PolicyListResponse getList(PolicyListRequest request);
    List<PolicyDTO> getAll();
    void assignPoliciesToUser(UUID userId, List<UUID> policyIds, UUID actorId);
    List<UUID> getPolicyIdsByUser(UUID userId);
    List<String> getUserPolicies(UUID userId);
}

