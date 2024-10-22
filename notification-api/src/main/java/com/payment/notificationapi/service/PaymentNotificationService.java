package com.payment.notificationapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.common.dto.NotificationDto;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.common.exception.CustomException;
import com.payment.common.exception.ErrorCode;
import com.payment.model.entity.Payment;
import com.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentNotificationService {
	private final PaymentRepository paymentRepository;

	@Transactional
	public void updateStatus(NotificationDto notificationDto, NotificationStatus status) {
		try {
			Payment payment = paymentRepository.findByOrderId(notificationDto.getOrderId())
				.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

			payment.setNotificationStatus(status);
			paymentRepository.save(payment);
			log.debug("Updated payment notification status to {} for orderId: {}",
				status, notificationDto.getOrderId());
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("Failed to update payment status", e);
			throw new CustomException(ErrorCode.NOTIFICATION_PAYMENT_UPDATE_FAILED);
		}
	}
}