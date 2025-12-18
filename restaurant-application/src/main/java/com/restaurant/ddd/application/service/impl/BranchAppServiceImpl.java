package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.BranchMapper;
import com.restaurant.ddd.application.model.branch.BranchDTO;
import com.restaurant.ddd.application.model.branch.BranchListRequest;
import com.restaurant.ddd.application.model.branch.BranchListResponse;
import com.restaurant.ddd.application.model.branch.CreateBranchRequest;
import com.restaurant.ddd.application.model.branch.UpdateBranchRequest;
import com.restaurant.ddd.application.service.BranchAppService;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Branch;
import com.restaurant.ddd.domain.respository.BranchRepository;
import com.restaurant.ddd.domain.service.BranchDomainService;
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
public class BranchAppServiceImpl implements BranchAppService {

    @Autowired
    private BranchDomainService branchDomainService;
    
    @Autowired
    private BranchRepository branchRepository;

    @Override
    @Transactional
    public BranchDTO create(CreateBranchRequest request, UUID userId) {
        log.info("Branch Application Service: create - {}", request.getName());
        
        // Validate uniqueness
        if (branchDomainService.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã chi nhánh đã tồn tại: " + request.getCode());
        }
        
        if (request.getEmail() != null && branchDomainService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng: " + request.getEmail());
        }
        
        if (request.getPhone() != null && branchDomainService.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại đã được sử dụng: " + request.getPhone());
        }
        
        Branch branch = BranchMapper.toEntity(request);
        
        // Validate business rules
        branch.validateCode();
        branch.validateName();
        branch.validateTime();
        
        branch.setCreatedBy(userId);
        branch.setUpdatedBy(userId);
        
        Branch saved = branchDomainService.save(branch);
        return BranchMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public BranchDTO update(UpdateBranchRequest request, UUID userId) {
        log.info("Branch Application Service: update - {}", request.getId());
        
        Branch branch = branchDomainService.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
        
        // Validate uniqueness if changed
        if (request.getCode() != null && !request.getCode().equals(branch.getCode())) {
            if (branchDomainService.existsByCode(request.getCode())) {
                throw new RuntimeException("Mã chi nhánh đã tồn tại: " + request.getCode());
            }
        }
        
        if (request.getEmail() != null && !request.getEmail().equals(branch.getEmail())) {
            if (branchDomainService.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng: " + request.getEmail());
            }
        }
        
        if (request.getPhone() != null && !request.getPhone().equals(branch.getPhone())) {
            if (branchDomainService.existsByPhone(request.getPhone())) {
                throw new RuntimeException("Số điện thoại đã được sử dụng: " + request.getPhone());
            }
        }
        
        BranchMapper.updateEntity(branch, request);
        
        // Validate business rules
        branch.validateCode();
        branch.validateName();
        branch.validateTime();
        
        branch.setUpdatedBy(userId);
        
        Branch updated = branchDomainService.save(branch);
        return BranchMapper.toDTO(updated);
    }

    @Override
    public BranchDTO getById(UUID id) {
        log.info("Branch Application Service: getById - {}", id);
        return branchDomainService.findById(id)
                .map(BranchMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
    }

    @Override
    public List<BranchDTO> getAll() {
        log.info("Branch Application Service: getAll");
        return branchDomainService.findAll().stream()
                .map(BranchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchDTO> getAllActive() {
        log.info("Branch Application Service: getAllActive");
        return branchDomainService.findByStatus(DataStatus.ACTIVE).stream()
                .map(BranchMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BranchListResponse getList(BranchListRequest request) {
        log.info("Branch Application Service: getList - keyword: {}, status: {}, page: {}, size: {}", 
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
        org.springframework.data.domain.Page<Branch> page = branchRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            pageable
        );
        
        // Map to DTOs
        BranchListResponse response = new BranchListResponse();
        response.setItems(page.getContent().stream().map(BranchMapper::toDTO).collect(Collectors.toList()));
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotal(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        
        return response;
    }

    @Override
    @Transactional
    public BranchDTO activate(UUID id, UUID userId) {
        log.info("Branch Application Service: activate - {}", id);
        
        Branch branch = branchDomainService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
        
        branch.activate();
        branch.setUpdatedBy(userId);
        
        Branch updated = branchDomainService.save(branch);
        return BranchMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public BranchDTO deactivate(UUID id, UUID userId) {
        log.info("Branch Application Service: deactivate - {}", id);
        
        Branch branch = branchDomainService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi nhánh"));
        
        branch.deactivate();
        branch.setUpdatedBy(userId);
        
        Branch updated = branchDomainService.save(branch);
        return BranchMapper.toDTO(updated);
    }
}
