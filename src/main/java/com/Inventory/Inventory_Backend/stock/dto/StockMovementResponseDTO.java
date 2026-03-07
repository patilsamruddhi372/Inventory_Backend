package com.Inventory.Inventory_Backend.stock.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementResponseDTO {

    private Long id;

    private Long itemId;

    private Long businessId;

    private BigDecimal quantity;

    private String movementType;

    private String referenceType;

    private Long referenceId;

    private String remarks;

    private LocalDateTime createdAt;
}