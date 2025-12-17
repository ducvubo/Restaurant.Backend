package com.restaurant.ddd.application.model.user;

import lombok.Data;

import java.util.List;

@Data
public class UserListResponse {
    private List<UserDTO> items;
    private Integer page;
    private Integer size;
    private Long total;
}
