package com.restaurant.ddd.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for PurchaseOrder - Đơn đặt hàng
 */
@Entity
@Table(name = "PURCHASE_ORDERS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PurchaseOrderJpaEntity extends BaseJpaEntity {

    @Column(name = "PO_CODE", nullable = false, unique = true, length = 50)
    private String poCode;

    @Column(name = "RFQ_ID")
    private UUID rfqId;

    @Column(name = "SUPPLIER_ID", nullable = false)
    private UUID supplierId;

    @Column(name = "WAREHOUSE_ID", nullable = false)
    private UUID warehouseId;

    @Column(name = "ORDER_DATE", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "EXPECTED_DELIVERY_DATE")
    private LocalDateTime expectedDeliveryDate;

    @Column(name = "PAYMENT_TERMS", length = 200)
    private String paymentTerms;

    @Column(name = "DELIVERY_TERMS", length = 200)
    private String deliveryTerms;

    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "RECEIVED_AMOUNT", precision = 18, scale = 2)
    private BigDecimal receivedAmount;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;
}
