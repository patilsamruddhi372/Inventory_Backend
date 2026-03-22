package com.Inventory.Inventory_Backend.party.repository;

import com.Inventory.Inventory_Backend.party.entity.Party;
import com.Inventory.Inventory_Backend.party.entity.PartyType; // ✅ added
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {

    // ---------------------------
    // Fetch Parties
    // ---------------------------

    List<Party> findByBusinessIdAndIsActiveTrue(Long businessId);

    Optional<Party> findByIdAndBusinessIdAndIsActiveTrue(Long id, Long businessId);

    boolean existsByIdAndBusinessIdAndIsActiveTrue(Long id, Long businessId);

    // ---------------------------
    // Duplicate Validation (CREATE)
    // ---------------------------

    boolean existsByBusinessIdAndGstinAndIsActiveTrue(Long businessId, String gstin);

    boolean existsByBusinessIdAndPhoneAndIsActiveTrue(Long businessId, String phone);

    boolean existsByBusinessIdAndEmailAndIsActiveTrue(Long businessId, String email);

    // ---------------------------
    // Duplicate Validation (UPDATE)
    // ---------------------------

    boolean existsByBusinessIdAndGstinAndIdNotAndIsActiveTrue(Long businessId, String gstin, Long id);

    boolean existsByBusinessIdAndPhoneAndIdNotAndIsActiveTrue(Long businessId, String phone, Long id);

    boolean existsByBusinessIdAndEmailAndIdNotAndIsActiveTrue(Long businessId, String email, Long id);

    // ---------------------------
    // ✅ NEW FILTER METHOD (FIX)
    // ---------------------------

    List<Party> findByBusinessIdAndTypeInAndIsActiveTrue(
            Long businessId,
            List<PartyType> types
    );
}