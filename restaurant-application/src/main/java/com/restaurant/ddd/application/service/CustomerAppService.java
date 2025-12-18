package com.restaurant.ddd.application.service;

import com.restaurant.ddd.application.model.customer.CreateCustomerRequest;
import com.restaurant.ddd.application.model.customer.CustomerDTO;
import com.restaurant.ddd.application.model.customer.CustomerListRequest;
import com.restaurant.ddd.application.model.customer.CustomerListResponse;
import com.restaurant.ddd.application.model.customer.UpdateCustomerRequest;
import com.restaurant.ddd.domain.model.ResultMessage;

import java.util.UUID;

public interface CustomerAppService {
    ResultMessage<CustomerDTO> createCustomer(CreateCustomerRequest request);
    ResultMessage<CustomerDTO> updateCustomer(UpdateCustomerRequest request);
    ResultMessage<CustomerDTO> getCustomer(UUID id);
    ResultMessage<CustomerListResponse> getList(CustomerListRequest request);
    ResultMessage<String> deactivateCustomer(UUID id);
    ResultMessage<String> activateCustomer(UUID id);
}
