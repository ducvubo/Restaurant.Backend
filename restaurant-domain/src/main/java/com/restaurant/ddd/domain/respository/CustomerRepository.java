package com.restaurant.ddd.domain.respository;

import com.restaurant.ddd.domain.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByCustomerCode(String customerCode);
    List<Customer> findAll();
    void delete(Customer customer);
    
    /**
     * Find all customers with filters and pagination
     */
    Page<Customer> findAll(
        String keyword,
        Integer status,
        Pageable pageable
    );
}
