package com.payment.processor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByPaymentId(String paymentId);
    List<Payment> findAllByOrderByPartitionAscOffsetAsc();
}
