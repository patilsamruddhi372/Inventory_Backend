package com.Inventory.Inventory_Backend.stock.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long businessId;

    private Long itemId;

    private BigDecimal quantity;

    private String movementType;

    private String referenceType;

    private Long referenceId;

    private String remarks;

    private LocalDateTime createdAt;
}