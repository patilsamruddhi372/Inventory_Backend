package com.Inventory.Inventory_Backend.item.dto;

import com.Inventory.Inventory_Backend.item.entity.Item;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ItemMapper {

    // =========================================================
    // REQUEST DTO → ENTITY (CREATE)
    // =========================================================
    public Item toEntity(ItemRequestDTO dto) {

        return Item.builder()
                .name(dto.getName())
                .type(dto.getType())
                .sku(dto.getSku())
                .category(defaultIfNull(dto.getCategory(), "General"))
                .brand(dto.getBrand())
                .unit(defaultIfNull(dto.getUnit(), resolveDefaultUnit(dto.getType())))
                .description(dto.getDescription())

                .salePrice(defaultIfNull(dto.getSalePrice(), BigDecimal.ZERO))
                .purchasePrice(defaultIfNull(dto.getPurchasePrice(), BigDecimal.ZERO))
                .mrpPrice(defaultIfNull(dto.getMrpPrice(), BigDecimal.ZERO))

                .openingStock(defaultIfNull(dto.getOpeningStock(), BigDecimal.ZERO))
                .lowStockAlert(defaultIfNull(dto.getLowStockAlert(), 0))

                .gstRate(defaultIfNull(dto.getGstRate(), BigDecimal.ZERO))
                .hsn(dto.getHsn())

                .saleDiscountPercent(defaultIfNull(dto.getSaleDiscountPercent(), BigDecimal.ZERO))
                .saleDiscountAmount(defaultIfNull(dto.getSaleDiscountAmount(), BigDecimal.ZERO))
                .purchaseDiscountPercent(defaultIfNull(dto.getPurchaseDiscountPercent(), BigDecimal.ZERO))
                .purchaseDiscountAmount(defaultIfNull(dto.getPurchaseDiscountAmount(), BigDecimal.ZERO))

                .isActive(defaultIfNull(dto.getIsActive(), true))
                .isFavorite(defaultIfNull(dto.getIsFavorite(), false))

                .build();
    }

    // =========================================================
    // UPDATE ENTITY
    // =========================================================
    public void updateEntity(Item entity, ItemRequestDTO dto) {

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getSku() != null) entity.setSku(dto.getSku());
        if (dto.getCategory() != null) entity.setCategory(dto.getCategory());
        if (dto.getBrand() != null) entity.setBrand(dto.getBrand());
        if (dto.getUnit() != null) entity.setUnit(dto.getUnit());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());

        if (dto.getSalePrice() != null) entity.setSalePrice(dto.getSalePrice());
        if (dto.getPurchasePrice() != null) entity.setPurchasePrice(dto.getPurchasePrice());
        if (dto.getMrpPrice() != null) entity.setMrpPrice(dto.getMrpPrice());

        if (dto.getOpeningStock() != null) entity.setOpeningStock(dto.getOpeningStock());
        if (dto.getLowStockAlert() != null) entity.setLowStockAlert(dto.getLowStockAlert());

        if (dto.getGstRate() != null) entity.setGstRate(dto.getGstRate());
        if (dto.getHsn() != null) entity.setHsn(dto.getHsn());

        if (dto.getSaleDiscountPercent() != null) entity.setSaleDiscountPercent(dto.getSaleDiscountPercent());
        if (dto.getSaleDiscountAmount() != null) entity.setSaleDiscountAmount(dto.getSaleDiscountAmount());
        if (dto.getPurchaseDiscountPercent() != null) entity.setPurchaseDiscountPercent(dto.getPurchaseDiscountPercent());
        if (dto.getPurchaseDiscountAmount() != null) entity.setPurchaseDiscountAmount(dto.getPurchaseDiscountAmount());

        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
        if (dto.getIsFavorite() != null) entity.setIsFavorite(dto.getIsFavorite());
    }

    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================
    public ItemResponseDTO toResponse(Item entity) {

        return ItemResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .sku(entity.getSku())
                .category(entity.getCategory())
                .brand(entity.getBrand())
                .unit(entity.getUnit())
                .description(entity.getDescription())

                .salePrice(defaultIfNull(entity.getSalePrice(), BigDecimal.ZERO))
                .purchasePrice(defaultIfNull(entity.getPurchasePrice(), BigDecimal.ZERO))
                .mrpPrice(defaultIfNull(entity.getMrpPrice(), BigDecimal.ZERO))

                .openingStock(defaultIfNull(entity.getOpeningStock(), BigDecimal.ZERO))
                .lowStockAlert(defaultIfNull(entity.getLowStockAlert(), 0))

                .gstRate(defaultIfNull(entity.getGstRate(), BigDecimal.ZERO))
                .hsn(entity.getHsn())

                .saleDiscountPercent(defaultIfNull(entity.getSaleDiscountPercent(), BigDecimal.ZERO))
                .saleDiscountAmount(defaultIfNull(entity.getSaleDiscountAmount(), BigDecimal.ZERO))
                .purchaseDiscountPercent(defaultIfNull(entity.getPurchaseDiscountPercent(), BigDecimal.ZERO))
                .purchaseDiscountAmount(defaultIfNull(entity.getPurchaseDiscountAmount(), BigDecimal.ZERO))

                .isActive(defaultIfNull(entity.getIsActive(), true))
                .isFavorite(defaultIfNull(entity.getIsFavorite(), false))

                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())

                .build();
    }

    // =========================================================
    // LIST MAPPING
    // =========================================================
    public List<ItemResponseDTO> toResponseList(List<Item> entities) {

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private String resolveDefaultUnit(String type) {
        return "service".equalsIgnoreCase(type) ? "Hour" : "Pcs";
    }

    private <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}