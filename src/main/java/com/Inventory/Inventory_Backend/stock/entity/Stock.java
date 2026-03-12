package com.Inventory.Inventory_Backend.stock.entity;

import com.Inventory.Inventory_Backend.item.entity.Item;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "stock",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_business_item_stock",
                        columnNames = {"business_id", "item_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    // ── Added for JPQL JOIN support ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", insertable = false, updatable = false)
    private Item item;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}