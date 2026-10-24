package com.sutra.textiles.repository;

import com.sutra.textiles.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUser_EmailIgnoreCase(
            Long orderId,
            String email
    );
}