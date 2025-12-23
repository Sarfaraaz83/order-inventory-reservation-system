package com.sheikh.orderinventorysystem.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderCleanupScheduler {

    private final OrderService orderService;

    public OrderCleanupScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    // Runs every 1 minute
    @Scheduled(fixedDelay = 60000)
    public void cleanupExpiredOrders() {
        orderService.cleanupExpiredOrders();
    }
}
