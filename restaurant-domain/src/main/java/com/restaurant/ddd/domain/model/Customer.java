package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.CustomerType;
import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class Customer {
    private UUID id;
    private String customerCode;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private CustomerType customerType;
    private DataStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không được rỗng");
        }
        if (customerType == null) {
            throw new IllegalArgumentException("Loại khách hàng không được rỗng");
        }
        if (customerType == CustomerType.COMPANY && (taxCode == null || taxCode.isBlank())) {
            throw new IllegalArgumentException("Mã số thuế không được rỗng đối với doanh nghiệp");
        }
    }
}
