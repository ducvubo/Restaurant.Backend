package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultMessage<T> implements Serializable {
    private ResultCode code;
    private String message;
    private T data;
}
