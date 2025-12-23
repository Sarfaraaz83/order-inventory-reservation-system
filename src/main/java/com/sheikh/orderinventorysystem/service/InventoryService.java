package com.sheikh.orderinventorysystem.service;

import com.sheikh.orderinventorysystem.domain.Inventory;
import com.sheikh.orderinventorysystem.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void reserveInventory(Long productId, int quantity) {

        Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found for product " + productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Insufficient inventory for product " + productId);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        inventoryRepository.save(inventory);
    }

    @Transactional
    public void releaseInventory(Long productId, int quantity) {

        Inventory inventory = inventoryRepository
                .findByProductIdForUpdate(productId)
                .orElseThrow(() -> new IllegalStateException("Inventory not found for product " + productId));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        inventoryRepository.save(inventory);
    }
}
