package com.restaurant.ddd.application.model.branch;

import com.restaurant.ddd.application.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BranchListRequest extends PageRequest {
    private String keyword;
    private Integer status;
}
