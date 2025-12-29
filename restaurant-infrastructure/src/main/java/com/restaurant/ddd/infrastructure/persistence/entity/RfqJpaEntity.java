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
 * JPA Entity for RequestForQuotation - Yêu cầu báo giá
 */
@Entity
@Table(name = "REQUEST_FOR_QUOTATIONS")
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RfqJpaEntity extends BaseJpaEntity {

    @Column(name = "RFQ_CODE", nullable = false, unique = true, length = 50)
    private String rfqCode;

    @Column(name = "REQUISITION_ID")
    private UUID requisitionId;

    @Column(name = "SUPPLIER_ID", nullable = false)
    private UUID supplierId;

    @Column(name = "SENT_DATE")
    private LocalDateTime sentDate;

    @Column(name = "VALID_UNTIL")
    private LocalDateTime validUntil;

    @Column(name = "TOTAL_AMOUNT", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "PAYMENT_TERMS", length = 200)
    private String paymentTerms;

    @Column(name = "DELIVERY_TERMS", length = 200)
    private String deliveryTerms;

    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;
}
