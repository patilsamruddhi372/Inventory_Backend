package com.Inventory.Inventory_Backend.stock.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponseDTO {

    private Long itemId;

    private Long businessId;

    private BigDecimal quantity;
}