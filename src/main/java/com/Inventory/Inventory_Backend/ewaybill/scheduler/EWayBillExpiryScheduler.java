package com.Inventory.Inventory_Backend.ewaybill.scheduler;

import com.Inventory.Inventory_Backend.ewaybill.repository.EWayBillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EWayBillExpiryScheduler {
    private final EWayBillRepository repository;

    @Scheduled(fixedRate = 300000) // runs every 5 minutes
    @Transactional
    public void expireEWayBills() {
        int updated = repository.expiredOldBills(LocalDateTime.now());

        if (updated > 0) {
            System.out.println("Expired EWay Bills: " + updated);
        }
    }
}
