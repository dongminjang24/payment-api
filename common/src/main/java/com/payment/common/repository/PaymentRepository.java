package com.payment.common.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.common.model.entity.Member;
import com.payment.common.model.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByCustomer(Member member);

	Optional<Payment> findByOrderId(String orderId);

	Optional<Payment> findByPaymentKeyAndCustomer_Email(String paymentKey, String email);

	Page<Payment> findAllByCustomer_Email(String email, Pageable pageable);
}