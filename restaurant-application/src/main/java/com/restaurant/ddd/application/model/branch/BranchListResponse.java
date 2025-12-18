package com.restaurant.ddd.application.model.branch;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BranchListResponse extends PageResponse<BranchDTO> {
}
