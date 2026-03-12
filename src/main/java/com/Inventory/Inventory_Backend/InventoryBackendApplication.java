package com.Inventory.Inventory_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class InventoryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryBackendApplication.class, args);
	}

}
