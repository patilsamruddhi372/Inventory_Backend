package com.Inventory.Inventory_Backend.item.repository;

import com.Inventory.Inventory_Backend.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByBusinessIdAndIsActiveTrue(Long businessId);

    Optional<Item> findByIdAndBusinessId(Long id, Long businessId);

    // ✅ safer: toggle only within same business, return updated row count
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Item i
           SET i.isFavorite = CASE
                               WHEN i.isFavorite = true THEN false
                               ELSE true
                             END
         WHERE i.id = :id
           AND i.businessId = :businessId
    """)
    int toggleFavorite(@Param("id") Long id, @Param("businessId") Long businessId);
}