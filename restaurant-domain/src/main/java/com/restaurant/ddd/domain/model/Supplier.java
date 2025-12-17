package com.restaurant.ddd.domain.model;

import com.restaurant.ddd.domain.enums.DataStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Supplier - Nhà cung cấp
 * Domain model for suppliers
 */
@Data
@Accessors(chain = true)
public class Supplier {
    private UUID id;
    private String code;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String taxCode;
    private String paymentTerms;
    private Integer rating; // 1-5 stars
    private String notes;
    private DataStatus status;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    /**
     * Validate supplier data
     */
    public void validate() {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được để trống");
        }
        validateContactInfo();
        validateRating();
    }

    /**
     * Validate contact information
     */
    public void validateContactInfo() {
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Email không hợp lệ");
            }
        }
        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("^[0-9]{10,11}$")) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (10-11 số)");
            }
        }
    }

    /**
     * Validate rating
     */
    public void validateRating() {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Đánh giá phải từ 1 đến 5 sao");
        }
    }

    /**
     * Check if supplier is highly rated
     */
    public boolean isHighRated() {
        return rating != null && rating >= 4;
    }

    /**
     * Activate supplier
     */
    public void activate() {
        this.status = DataStatus.ACTIVE;
    }

    /**
     * Deactivate supplier
     */
    public void deactivate() {
        this.status = DataStatus.INACTIVE;
    }
}
