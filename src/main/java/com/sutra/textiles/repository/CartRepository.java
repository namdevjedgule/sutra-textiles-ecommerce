package com.sutra.textiles.repository;

import com.sutra.textiles.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser_EmailIgnoreCase(String email);
}
