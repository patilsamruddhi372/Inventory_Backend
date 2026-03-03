package com.Inventory.Inventory_Backend.party.repository;

import com.Inventory.Inventory_Backend.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {

    //only fetch active parties
    List<Party> findByBusinessIdAndIsActiveTrue(Long businessId);

    Optional<Party> findByIdAndBusinessIdAndIsActiveTrue(Long id, Long businessId);
}

