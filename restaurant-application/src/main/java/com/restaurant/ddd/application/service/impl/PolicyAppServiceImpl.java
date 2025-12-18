package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.PolicyMapper;
import com.restaurant.ddd.application.model.user.CreatePolicyRequest;
import com.restaurant.ddd.application.model.user.PolicyDTO;
import com.restaurant.ddd.application.model.user.PolicyListRequest;
import com.restaurant.ddd.application.model.user.PolicyListResponse;
import com.restaurant.ddd.application.model.user.UpdatePolicyRequest;
import com.restaurant.ddd.application.service.PolicyAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Policy;
import com.restaurant.ddd.domain.model.UserPolicy;
import com.restaurant.ddd.domain.respository.PolicyRepository;
import com.restaurant.ddd.domain.service.PolicyDomainService;
import com.restaurant.ddd.domain.service.UserPolicyDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PolicyAppServiceImpl implements PolicyAppService {

    @Autowired
    private PolicyDomainService policyDomainService;
    
    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserPolicyDomainService userPolicyDomainService;

    @Override
    @Transactional
    public PolicyDTO create(CreatePolicyRequest request, UUID userId) {
        log.info("Policy Application Service: create - {}", request.getName());
        Policy policy = PolicyMapper.toEntity(request);
        policy.setCreatedBy(userId);
        policy.setUpdatedBy(userId);
        Policy saved = policyDomainService.save(policy);
        return PolicyMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public PolicyDTO update(UpdatePolicyRequest request, UUID userId) {
        log.info("Policy Application Service: update - {}", request.getId());
        Policy policy = policyDomainService.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tập quyền để cập nhật"));
        
        PolicyMapper.updateEntity(policy, request);
        policy.setUpdatedBy(userId);
        Policy updated = policyDomainService.save(policy);
        return PolicyMapper.toDTO(updated);
    }

    @Override
    public PolicyDTO getById(UUID id) {
        log.info("Policy Application Service: getById - {}", id);
        return policyDomainService.findById(id)
                .map(PolicyMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tập quyền"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Policy Application Service: delete - {}", id);
        if (!policyDomainService.findById(id).isPresent()) {
            throw new RuntimeException("Không tìm thấy tập quyền để xóa");
        }
        policyDomainService.deleteById(id);
    }

    @Override
    public PolicyListResponse getList(PolicyListRequest request) {
        log.info("Policy Application Service: getList - keyword: {}, status: {}, page: {}, size: {}", 
                request.getKeyword(), request.getStatus(), request.getPage(), request.getSize());
        
        // Build Pageable with sorting
        String sortField = request.getSortBy() != null ? request.getSortBy() : "createdDate";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
        
        org.springframework.data.domain.Sort.Direction direction = 
            "asc".equalsIgnoreCase(sortDirection) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPage() - 1,
            request.getSize(),
            org.springframework.data.domain.Sort.by(direction, sortField)
        );
        
        // Call repository with filters
        org.springframework.data.domain.Page<Policy> page = policyRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            pageable
        );
        
        // Map to DTOs
        PolicyListResponse response = new PolicyListResponse();
        response.setItems(page.getContent().stream().map(PolicyMapper::toDTO).collect(Collectors.toList()));
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotal(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        
        return response;
    }

    @Override
    public List<PolicyDTO> getAll() {
        log.info("Policy Application Service: getAll");
        return policyDomainService.findByStatus(DataStatus.ACTIVE)
                .stream()
                .map(PolicyMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPoliciesToUser(UUID userId, List<UUID> policyIds, UUID actorId) {
        log.info("Policy Application Service: assignPoliciesToUser - userId: {}, policyIds: {}", userId, policyIds);
        
        // Xóa các mapping cũ
        userPolicyDomainService.deleteByUserId(userId);
        
        // Tạo mapping mới
        if (policyIds != null && !policyIds.isEmpty()) {
            List<UUID> distinctPolicyIds = policyIds.stream().distinct().collect(Collectors.toList());
            for (UUID policyId : distinctPolicyIds) {
                UserPolicy userPolicy = new UserPolicy();
                userPolicy.setUserId(userId);
                userPolicy.setPolicyId(policyId);
                userPolicy.setCreatedBy(actorId);
                userPolicy.setUpdatedBy(actorId);
                userPolicyDomainService.save(userPolicy);
            }
        }
    }

    @Override
    public List<UUID> getPolicyIdsByUser(UUID userId) {
        log.info("Policy Application Service: getPolicyIdsByUser - userId: {}", userId);
        return userPolicyDomainService.findPolicyIdsByUserId(userId);
    }

    @Override
    public List<String> getUserPolicies(UUID userId) {
        log.info("Policy Application Service: getUserPolicies - userId: {}", userId);
        List<UUID> policyIds = userPolicyDomainService.findPolicyIdsByUserId(userId);
        List<String> allPolicies = new ArrayList<>();
        
        for (UUID policyId : policyIds) {
            policyDomainService.findById(policyId).ifPresent(policy -> {
                List<String> policies = policy.getPolicies();
                if (policies != null) {
                    allPolicies.addAll(policies);
                }
            });
        }
        
        return allPolicies.stream().distinct().collect(Collectors.toList());
    }
}
