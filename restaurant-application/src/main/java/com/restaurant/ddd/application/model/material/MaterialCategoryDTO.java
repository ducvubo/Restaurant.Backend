package com.restaurant.ddd.application.model.material;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MaterialCategoryDTO {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private DataStatus status;
    private LocalDateTime createdDate;
}
