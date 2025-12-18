package com.restaurant.ddd.application.service.impl;

import com.restaurant.ddd.application.mapper.CustomerMapper;
import com.restaurant.ddd.application.model.customer.CreateCustomerRequest;
import com.restaurant.ddd.application.model.customer.CustomerDTO;
import com.restaurant.ddd.application.model.customer.CustomerListResponse;
import com.restaurant.ddd.application.model.customer.UpdateCustomerRequest;
import com.restaurant.ddd.application.service.CustomerAppService;
import com.restaurant.ddd.domain.enums.CustomerType;
import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.enums.ResultCode;
import com.restaurant.ddd.domain.model.Customer;
import com.restaurant.ddd.domain.model.ResultMessage;
import com.restaurant.ddd.domain.respository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerAppServiceImpl implements CustomerAppService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public ResultMessage<CustomerDTO> createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setCustomerCode(generateCustomerCode());
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setTaxCode(request.getTaxCode());
        customer.setCustomerType(CustomerType.fromCode(request.getCustomerType()));
        customer.setStatus(DataStatus.ACTIVE);
        customer.setCreatedDate(LocalDateTime.now());

        customer.validate();
        Customer saved = customerRepository.save(customer);

        return new ResultMessage<>(ResultCode.SUCCESS, "Tạo khách hàng thành công", CustomerMapper.toDTO(saved));
    }

    @Override
    @Transactional
    public ResultMessage<CustomerDTO> updateCustomer(UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(request.getId())
                .orElse(null);
        
        if (customer == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Khách hàng không tồn tại", null);
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setTaxCode(request.getTaxCode());
        customer.setCustomerType(CustomerType.fromCode(request.getCustomerType()));
        customer.setUpdatedDate(LocalDateTime.now());

        customer.validate();
        Customer updated = customerRepository.save(customer);

        return new ResultMessage<>(ResultCode.SUCCESS, "Cập nhật khách hàng thành công", CustomerMapper.toDTO(updated));
    }

    @Override
    public ResultMessage<CustomerDTO> getCustomer(UUID id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Khách hàng không tồn tại", null);
        }
        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy thông tin khách hàng thành công", CustomerMapper.toDTO(customer));
    }

    @Override
    public ResultMessage<CustomerListResponse> getList(com.restaurant.ddd.application.model.customer.CustomerListRequest request) {
        // Build Pageable with sorting
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "name";
        org.springframework.data.domain.Sort.Direction direction = 
            "ASC".equalsIgnoreCase(request.getSafeSortDirection()) 
                ? org.springframework.data.domain.Sort.Direction.ASC 
                : org.springframework.data.domain.Sort.Direction.DESC;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
            request.getPageZeroBased(),
            request.getSafeSize(),
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
        
        // Query with pagination
        org.springframework.data.domain.Page<Customer> page = customerRepository.findAll(
            request.getKeyword(),
            request.getStatus(),
            pageable
        );
        
        // Map to DTOs
        List<CustomerDTO> dtos = page.getContent().stream()
            .map(CustomerMapper::toDTO)
            .collect(Collectors.toList());
        
        CustomerListResponse response = new CustomerListResponse();
        response.setItems(dtos);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setSize(request.getSafeSize());
        response.setTotalPages(page.getTotalPages());

        return new ResultMessage<>(ResultCode.SUCCESS, "Lấy danh sách khách hàng thành công", response);
    }

    @Override
    @Transactional
    public ResultMessage<String> deactivateCustomer(UUID id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Khách hàng không tồn tại", null);
        }

        customer.setStatus(DataStatus.INACTIVE);
        customerRepository.save(customer);

        return new ResultMessage<>(ResultCode.SUCCESS, "Vô hiệu hóa khách hàng thành công", null);
    }

    @Override
    @Transactional
    public ResultMessage<String> activateCustomer(UUID id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return new ResultMessage<>(ResultCode.ERROR, "Khách hàng không tồn tại", null);
        }

        customer.setStatus(DataStatus.ACTIVE);
        customerRepository.save(customer);

        return new ResultMessage<>(ResultCode.SUCCESS, "Kích hoạt khách hàng thành công", null);
    }

    private String generateCustomerCode() {
        return "KH" + System.currentTimeMillis();
    }
}
