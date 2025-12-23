package com.sheikh.orderinventorysystem.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "reservation_expiry", nullable = false)
    private LocalDateTime reservationExpiry;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Order() {
        // JPA
    }

    public Order(Long productId, Integer quantity, OrderStatus status, LocalDateTime reservationExpiry) {
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.reservationExpiry = reservationExpiry;
        this.createdAt = LocalDateTime.now();
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getReservationExpiry() {
        return reservationExpiry;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
