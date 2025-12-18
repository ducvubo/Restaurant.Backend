package com.restaurant.ddd.infrastructure.persistence.repository.impl;

import com.restaurant.ddd.domain.model.Customer;
import com.restaurant.ddd.domain.respository.CustomerRepository;
import com.restaurant.ddd.infrastructure.persistence.entity.CustomerJpaEntity;
import com.restaurant.ddd.infrastructure.persistence.mapper.CustomerDataAccessMapper;
import com.restaurant.ddd.infrastructure.persistence.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = customerDataAccessMapper.toEntity(customer);
        CustomerJpaEntity saved = customerJpaRepository.save(entity);
        return customerDataAccessMapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerJpaRepository.findById(id)
                .map(customerDataAccessMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByCustomerCode(String customerCode) {
        return customerJpaRepository.findByCustomerCode(customerCode)
                .map(customerDataAccessMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return customerJpaRepository.findAll().stream()
                .map(customerDataAccessMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Customer customer) {
        customerJpaRepository.deleteById(customer.getId());
    }
    
    @Override
    public org.springframework.data.domain.Page<Customer> findAll(
            String keyword,
            Integer status,
            org.springframework.data.domain.Pageable pageable) {
        
        return customerJpaRepository.findAll(
                com.restaurant.ddd.infrastructure.persistence.specification.CustomerSpecification.buildSpec(keyword, status),
                pageable
        ).map(customerDataAccessMapper::toDomain);
    }
}
