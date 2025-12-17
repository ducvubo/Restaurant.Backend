package com.restaurant.ddd.domain.service.impl;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Branch;
import com.restaurant.ddd.domain.respository.BranchRepository;
import com.restaurant.ddd.domain.service.BranchDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class BranchDomainServiceImpl implements BranchDomainService {

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public Optional<Branch> findById(UUID id) {
        log.info("Branch Domain Service: findById - {}", id);
        return branchRepository.findById(id);
    }

    @Override
    public List<Branch> findAll() {
        log.info("Branch Domain Service: findAll");
        return branchRepository.findAll();
    }

    @Override
    public List<Branch> findByStatus(DataStatus status) {
        log.info("Branch Domain Service: findByStatus - {}", status);
        return branchRepository.findByStatus(status);
    }

    @Override
    public Branch save(Branch branch) {
        log.info("Branch Domain Service: save - {}", branch.getName());
        return branchRepository.save(branch);
    }

    @Override
    public boolean existsByCode(String code) {
        log.info("Branch Domain Service: existsByCode - {}", code);
        return branchRepository.existsByCode(code);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.info("Branch Domain Service: existsByEmail - {}", email);
        return branchRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhone(String phone) {
        log.info("Branch Domain Service: existsByPhone - {}", phone);
        return branchRepository.existsByPhone(phone);
    }
}
