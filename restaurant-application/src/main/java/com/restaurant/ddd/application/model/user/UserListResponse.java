package com.restaurant.ddd.application.model.user;

import com.restaurant.ddd.application.model.common.PageResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserListResponse extends PageResponse<UserDTO> {
}
