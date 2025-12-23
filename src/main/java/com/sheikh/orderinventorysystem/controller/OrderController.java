package com.sheikh.orderinventorysystem.controller;

import com.sheikh.orderinventorysystem.controller.dto.PlaceOrderRequest;
import com.sheikh.orderinventorysystem.domain.Order;
import com.sheikh.orderinventorysystem.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Place order
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody PlaceOrderRequest request) {
        Order order = orderService.placeOrder(
                request.getProductId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(order);
    }

    // Submit payment
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<String> submitPayment(@PathVariable Long orderId) {
        orderService.submitPayment(orderId);
        return ResponseEntity.ok("Payment successful");
    }
}
