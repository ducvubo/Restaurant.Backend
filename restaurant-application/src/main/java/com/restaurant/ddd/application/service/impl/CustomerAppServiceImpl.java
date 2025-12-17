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
    public ResultMessage<CustomerListResponse> getList() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> dtos = customers.stream()
                .map(CustomerMapper::toDTO)
                .collect(Collectors.toList());

        CustomerListResponse response = new CustomerListResponse();
        response.setItems(dtos);
        response.setTotal(dtos.size());

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
