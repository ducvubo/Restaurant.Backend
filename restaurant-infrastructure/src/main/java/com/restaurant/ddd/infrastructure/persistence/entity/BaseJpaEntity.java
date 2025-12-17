package com.restaurant.ddd.infrastructure.persistence.entity;

import com.restaurant.ddd.domain.enums.DataStatus;
import com.restaurant.ddd.domain.model.converter.DataStatusConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class BaseJpaEntity {

    @Id
    @Column(name = "ID", columnDefinition = "UUID", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "CREATED_BY", columnDefinition = "UUID")
    private UUID createdBy;

    @Column(name = "UPDATED_BY", columnDefinition = "UUID")
    private UUID updatedBy;

    @Column(name = "DELETED_BY", columnDefinition = "UUID")
    private UUID deletedBy;

    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @Column(name = "DELETED_DATE")
    private LocalDateTime deletedDate;

    @Column(name = "STATUS", nullable = false)
    @Convert(converter = DataStatusConverter.class)
    private DataStatus status = DataStatus.ACTIVE;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        if (updatedDate == null) {
            updatedDate = LocalDateTime.now();
        }
        if (status == null) {
            status = DataStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}


