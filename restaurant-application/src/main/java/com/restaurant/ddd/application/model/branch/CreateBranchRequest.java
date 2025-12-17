package com.restaurant.ddd.application.model.branch;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateBranchRequest {
    private String code;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private DataStatus status;
}
