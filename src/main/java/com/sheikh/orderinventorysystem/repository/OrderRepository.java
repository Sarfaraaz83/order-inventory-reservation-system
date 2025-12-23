package com.sheikh.orderinventorysystem.repository;

import com.sheikh.orderinventorysystem.domain.Order;
import com.sheikh.orderinventorysystem.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId")
    Optional<Order> findByIdForUpdate(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o " +
            "WHERE o.status = :status " +
            "AND o.reservationExpiry < :now")
    List<Order> findExpiredOrders(@Param("status") OrderStatus status,
                                  @Param("now") LocalDateTime now);
}
