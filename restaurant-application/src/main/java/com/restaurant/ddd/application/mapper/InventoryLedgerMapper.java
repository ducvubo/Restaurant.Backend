package com.restaurant.ddd.application.mapper;

import com.restaurant.ddd.application.model.ledger.InventoryLedgerDTO;
import com.restaurant.ddd.domain.model.InventoryLedger;

import java.math.BigDecimal;

public class InventoryLedgerMapper {

    public static InventoryLedgerDTO toDTO(InventoryLedger ledger) {
        if (ledger == null) return null;
        
        InventoryLedgerDTO dto = new InventoryLedgerDTO();
        dto.setId(ledger.getId());
        dto.setWarehouseId(ledger.getWarehouseId());
        dto.setMaterialId(ledger.getMaterialId());
        dto.setTransactionId(ledger.getTransactionId());
        dto.setTransactionCode(ledger.getTransactionCode());
        dto.setTransactionDate(ledger.getTransactionDate());
        dto.setInventoryMethod(ledger.getInventoryMethod() != null ? ledger.getInventoryMethod().name() : null);
        dto.setQuantity(ledger.getQuantity());
        dto.setUnitId(ledger.getUnitId());
        dto.setUnitPrice(ledger.getUnitPrice());
        dto.setRemainingQuantity(ledger.getRemainingQuantity());
        dto.setBatchNumber(ledger.getBatchNumber());
        dto.setCreatedDate(ledger.getCreatedDate());
        
        // Calculate quantityOut: quantity - remainingQuantity = amount that was deducted
        if (ledger.getQuantity() != null && ledger.getRemainingQuantity() != null) {
            BigDecimal deducted = ledger.getQuantity().subtract(ledger.getRemainingQuantity());
            dto.setQuantityOut(deducted.compareTo(BigDecimal.ZERO) > 0 ? deducted : BigDecimal.ZERO);
        } else {
            dto.setQuantityOut(BigDecimal.ZERO);
        }
        
        return dto;
    }
}
