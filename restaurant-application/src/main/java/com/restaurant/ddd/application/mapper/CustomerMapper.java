package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.customer.CustomerDTO;
import com.restaurant.ddd.domain.model.Customer;

public class CustomerMapper {
    
    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setTaxCode(customer.getTaxCode());
        dto.setCustomerType(customer.getCustomerType().code());
        dto.setCustomerTypeName(customer.getCustomerType().message());
        dto.setStatus(customer.getStatus().code());
        dto.setCreatedDate(customer.getCreatedDate());
        dto.setUpdatedDate(customer.getUpdatedDate());

        return dto;
    }
}
