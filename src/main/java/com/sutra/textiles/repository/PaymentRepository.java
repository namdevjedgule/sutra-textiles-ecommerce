package com.sutra.textiles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sutra.textiles.entity.Payment;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {
}
