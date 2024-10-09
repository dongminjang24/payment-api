package com.payment.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.model.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Optional<Notification> findByOrderId(String orderId);
}
