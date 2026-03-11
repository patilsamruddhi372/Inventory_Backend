package com.Inventory.Inventory_Backend.item.repository;

import com.Inventory.Inventory_Backend.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // =========================
    // FETCH ALL ACTIVE ITEMS
    // =========================
    List<Item> findByBusinessIdAndIsActiveTrue(Long businessId);

    // =========================
    // FETCH ITEM BY ID + BUSINESS
    // =========================
    Optional<Item> findByIdAndBusinessId(Long id, Long businessId);

    // =========================
    // CHECK ITEM EXISTS
    // =========================
    boolean existsByIdAndBusinessIdAndIsActiveTrue(Long id, Long businessId);

    // ⭐ ADD THIS METHOD
    boolean existsByNameIgnoreCaseAndBusinessIdAndIsActiveTrue(String name, Long businessId);

    // =========================
    // TOGGLE FAVORITE
    // =========================
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Item i
           SET i.isFavorite =
               CASE
                   WHEN i.isFavorite = true THEN false
                   ELSE true
               END
         WHERE i.id = :id
           AND i.businessId = :businessId
    """)
    int toggleFavorite(
            @Param("id") Long id,
            @Param("businessId") Long businessId
    );
}