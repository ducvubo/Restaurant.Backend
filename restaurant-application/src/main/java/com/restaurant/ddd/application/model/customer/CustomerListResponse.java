package com.restaurant.ddd.application.model.customer;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerListResponse extends PageResponse<CustomerDTO> {
    // Can add specific fields if needed
}
