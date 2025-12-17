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
        
        List<Branch> allBranches = branchDomainService.findAll();
        
        // Filter by keyword
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = request.getKeyword().toLowerCase();
            allBranches = allBranches.stream()
                    .filter(b -> (b.getName() != null && b.getName().toLowerCase().contains(keyword)) ||
                                (b.getCode() != null && b.getCode().toLowerCase().contains(keyword)) ||
                                (b.getEmail() != null && b.getEmail().toLowerCase().contains(keyword)) ||
                                (b.getPhone() != null && b.getPhone().toLowerCase().contains(keyword)) ||
                                (b.getAddress() != null && b.getAddress().toLowerCase().contains(keyword)))
                    .collect(Collectors.toList());
        }
        
        // Filter by status
        if (request.getStatus() != null) {
            allBranches = allBranches.stream()
                    .filter(b -> b.getStatus() != null && b.getStatus().code().equals(request.getStatus()))
                    .collect(Collectors.toList());
        }
        
        // Pagination
        int page = request.getPage() != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 10;
        int total = allBranches.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        
        List<Branch> pagedBranches = start < total ? allBranches.subList(start, end) : new ArrayList<>();
        
        BranchListResponse response = new BranchListResponse();
        response.setItems(pagedBranches.stream().map(BranchMapper::toDTO).collect(Collectors.toList()));
        response.setPage(request.getPage() != null && request.getPage() > 0 ? request.getPage() : 1);
        response.setSize(size);
        response.setTotal((long) total);
        
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
