package com.Inventory.Inventory_Backend.item.dto;

import com.Inventory.Inventory_Backend.item.entity.Item;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ItemMapper {

    // ═══════════════════════════════════════════════
    // REQUEST DTO → ENTITY (create)
    // ═══════════════════════════════════════════════
    public Item toEntity(ItemRequestDTO dto) {
        return Item.builder()
                .name(dto.getName())
                .type(dto.getType())
                .sku(dto.getSku())
                .category(defaultIfNull(dto.getCategory(), "General"))
                .brand(dto.getBrand())
                .unit(defaultIfNull(dto.getUnit(), resolveDefaultUnit(dto.getType())))
                .salePrice(defaultIfNull(dto.getSalePrice(), BigDecimal.ZERO))
                .purchasePrice(defaultIfNull(dto.getPurchasePrice(), BigDecimal.ZERO))
                .mrpPrice(defaultIfNull(dto.getMrpPrice(), BigDecimal.ZERO))
                .stock(defaultIfNull(dto.getStock(), 0))
                .lowStockAlert(defaultIfNull(dto.getLowStockAlert(), 0))
                .gstRate(defaultIfNull(dto.getGstRate(), BigDecimal.ZERO))
                .hsn(dto.getHsn())
                .isActive(defaultIfNull(dto.getIsActive(), true))
                .isFavorite(defaultIfNull(dto.getIsFavorite(), false))
                .build();
    }

    // ═══════════════════════════════════════════════
    // REQUEST DTO → EXISTING ENTITY (update)
    // ═══════════════════════════════════════════════
    public void updateEntity(Item entity, ItemRequestDTO dto) {
        if (dto.getName() != null)          entity.setName(dto.getName());
        if (dto.getType() != null)          entity.setType(dto.getType());
        if (dto.getSku() != null)           entity.setSku(dto.getSku());
        if (dto.getCategory() != null)      entity.setCategory(dto.getCategory());
        if (dto.getBrand() != null)         entity.setBrand(dto.getBrand());
        if (dto.getUnit() != null)          entity.setUnit(dto.getUnit());
        if (dto.getSalePrice() != null)     entity.setSalePrice(dto.getSalePrice());
        if (dto.getPurchasePrice() != null) entity.setPurchasePrice(dto.getPurchasePrice());
        if (dto.getMrpPrice() != null)      entity.setMrpPrice(dto.getMrpPrice());
        if (dto.getStock() != null)         entity.setStock(dto.getStock());
        if (dto.getLowStockAlert() != null) entity.setLowStockAlert(dto.getLowStockAlert());
        if (dto.getGstRate() != null)       entity.setGstRate(dto.getGstRate());
        if (dto.getHsn() != null)           entity.setHsn(dto.getHsn());
        if (dto.getIsActive() != null)      entity.setIsActive(dto.getIsActive());
        if (dto.getIsFavorite() != null)    entity.setIsFavorite(dto.getIsFavorite());
    }

    // ═══════════════════════════════════════════════
    // ENTITY → SINGLE RESPONCE DTO
    // ═══════════════════════════════════════════════
    public itemResponceDTO toResponce(Item entity) {                  // ✅ single
        BigDecimal salePrice = defaultIfNull(entity.getSalePrice(), BigDecimal.ZERO);
        int stock = defaultIfNull(entity.getStock(), 0);
        int lowAlert = defaultIfNull(entity.getLowStockAlert(), 0);

        return itemResponceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .sku(entity.getSku())
                .category(entity.getCategory())
                .brand(entity.getBrand())
                .unit(entity.getUnit())
                .salePrice(salePrice)
                .purchasePrice(defaultIfNull(entity.getPurchasePrice(), BigDecimal.ZERO))
                .mrpPrice(defaultIfNull(entity.getMrpPrice(), BigDecimal.ZERO))
                .stock(stock)
                .lowStockAlert(lowAlert)
                .gstRate(defaultIfNull(entity.getGstRate(), BigDecimal.ZERO))
                .hsn(entity.getHsn())
                .isActive(defaultIfNull(entity.getIsActive(), true))
                .isFavorite(defaultIfNull(entity.getIsFavorite(), false))
                .stockValue(salePrice.multiply(BigDecimal.valueOf(stock)))
                .stockStatus(resolveStockStatus(entity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ═══════════════════════════════════════════════
    // ENTITY LIST → RESPONCE DTO LIST
    // ═══════════════════════════════════════════════
    public List<itemResponceDTO> toResponceList(List<Item> entities) { // ✅ list
        return entities.stream()
                .map(this::toResponce)
                .toList();
    }

    // ═══════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════
    private String resolveStockStatus(Item item) {
        if ("service".equalsIgnoreCase(item.getType())) {
            return "inStock";
        }
        int stock = defaultIfNull(item.getStock(), 0);
        int lowAlert = defaultIfNull(item.getLowStockAlert(), 0);

        if (stock == 0)                          return "outOfStock";
        if (lowAlert > 0 && stock <= lowAlert)   return "lowStock";
        return "inStock";
    }

    private String resolveDefaultUnit(String type) {
        return "service".equalsIgnoreCase(type) ? "Hour" : "Pcs";
    }

    private <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}