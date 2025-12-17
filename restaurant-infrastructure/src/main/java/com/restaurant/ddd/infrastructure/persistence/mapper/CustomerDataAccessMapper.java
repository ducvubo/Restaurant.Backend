package com.restaurant.ddd.infrastructure.persistence.mapper;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.Customer;
import com.restaurant.ddd.infrastructure.persistence.entity.CustomerJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public CustomerJpaEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(customer.getId());
        entity.setCustomerCode(customer.getCustomerCode());
        entity.setName(customer.getName());
        entity.setPhone(customer.getPhone());
        entity.setEmail(customer.getEmail());
        entity.setAddress(customer.getAddress());
        entity.setTaxCode(customer.getTaxCode());
        entity.setCustomerType(customer.getCustomerType());
        entity.setStatus(customer.getStatus() != null ? customer.getStatus() : DataStatus.ACTIVE);
        entity.setCreatedDate(customer.getCreatedDate());
        entity.setUpdatedDate(customer.getUpdatedDate());

        return entity;
    }

    public Customer toDomain(CustomerJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId(entity.getId());
        customer.setCustomerCode(entity.getCustomerCode());
        customer.setName(entity.getName());
        customer.setPhone(entity.getPhone());
        customer.setEmail(entity.getEmail());
        customer.setAddress(entity.getAddress());
        customer.setTaxCode(entity.getTaxCode());
        customer.setCustomerType(entity.getCustomerType());
        customer.setStatus(entity.getStatus());
        customer.setCreatedDate(entity.getCreatedDate());
        customer.setUpdatedDate(entity.getUpdatedDate());

        return customer;
    }
}
