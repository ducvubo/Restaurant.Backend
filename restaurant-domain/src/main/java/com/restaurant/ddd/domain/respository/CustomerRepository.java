package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByCustomerCode(String customerCode);
    List<Customer> findAll();
    void delete(Customer customer);
}
