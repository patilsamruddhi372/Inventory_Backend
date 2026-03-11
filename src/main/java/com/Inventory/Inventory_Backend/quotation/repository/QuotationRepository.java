package com.Inventory.Inventory_Backend.quotation.repository;

import com.Inventory.Inventory_Backend.quotation.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    List<Quotation> findByBusinessIdAndIsDeletedFalse(Long businessId);

    Optional<Quotation> findByIdAndBusinessIdAndIsDeletedFalse(Long id, Long businessId);

}