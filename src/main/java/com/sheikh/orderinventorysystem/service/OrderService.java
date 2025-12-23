package com.sheikh.orderinventorysystem.service;

import com.sheikh.orderinventorysystem.domain.Order;
import com.sheikh.orderinventorysystem.domain.OrderStatus;
import com.sheikh.orderinventorysystem.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;

    public OrderService(InventoryService inventoryService,
                        OrderRepository orderRepository) {
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order placeOrder(Long productId, int quantity) {

        // 1. Reserve inventory FIRST (this acquires DB lock)
        inventoryService.reserveInventory(productId, quantity);

        // 2. Create order with TTL
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2);

        Order order = new Order(
                productId,
                quantity,
                OrderStatus.CREATED,
                expiryTime
        );

        // 3. Persist order
        return orderRepository.save(order);
    }

    @Transactional
    public void submitPayment(Long orderId) {

        // 1. Lock the order row
        Order order = orderRepository
                .findByIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found"));

        // 2. If already processed, do nothing
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return;
        }

        // 3. Check TTL
        if (order.getReservationExpiry().isBefore(LocalDateTime.now())) {
            order.setStatus(OrderStatus.EXPIRED);
            throw new IllegalStateException("Order expired. Payment rejected.");
        }

        // 4. Mark as confirmed
        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);
    }

    @Transactional
    public void cleanupExpiredOrders() {

        var expiredOrders = orderRepository.findExpiredOrders(
                OrderStatus.CREATED,
                LocalDateTime.now()
        );

        for (Order order : expiredOrders) {

            // Lock order row to avoid race with payment
            Order lockedOrder = orderRepository
                    .findByIdForUpdate(order.getOrderId())
                    .orElseThrow();

            // Re-check status (safety)
            if (lockedOrder.getStatus() != OrderStatus.CREATED) {
                continue;
            }

            // 1. Mark order expired
            lockedOrder.setStatus(OrderStatus.EXPIRED);
            orderRepository.save(lockedOrder);

            // 2. Release inventory
            inventoryService.releaseInventory(
                    lockedOrder.getProductId(),
                    lockedOrder.getQuantity()
            );
        }
    }

}
