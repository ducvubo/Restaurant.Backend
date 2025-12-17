package com.restaurant.ddd.domain.enums;

/**
 * Interface cho các enum có code (số) để serialize/deserialize thành số thay vì string
 */
public interface CodeEnum {
    /**
     * Trả về code (số) của enum
     */
    Integer code();
}

