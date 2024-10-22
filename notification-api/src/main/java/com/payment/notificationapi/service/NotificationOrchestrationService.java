package com.payment.notificationapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.common.dto.NotificationDto;
import com.payment.common.enum_type.NotificationStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationOrchestrationService {
	private final NotificationStatusService notificationStatusService;
	private final PaymentNotificationService paymentNotificationService;

	@Transactional
	public void processInitialStatus(NotificationDto notificationDto) {
		try {
			notificationStatusService.saveStatus(notificationDto, NotificationStatus.PENDING);
			paymentNotificationService.updateStatus(notificationDto, NotificationStatus.PENDING);
			log.info("Successfully processed initial status for orderId: {}", notificationDto.getOrderId());
		} catch (Exception e) {
			log.error("Failed to process initial status for orderId: {}", notificationDto.getOrderId(), e);
			throw e;
		}
	}

	@Transactional
	public void processSuccessStatus(NotificationDto notificationDto) {
		try {
			notificationStatusService.saveStatus(notificationDto, NotificationStatus.SUCCESS);
			paymentNotificationService.updateStatus(notificationDto, NotificationStatus.SUCCESS);
			log.info("Successfully processed success status for orderId: {}", notificationDto.getOrderId());
		} catch (Exception e) {
			log.error("Failed to process success status for orderId: {}", notificationDto.getOrderId(), e);
			throw e;
		}
	}

	@Transactional
	public void processFailureStatus(NotificationDto notificationDto) {
		try {
			notificationStatusService.saveStatus(notificationDto, NotificationStatus.FAILURE);
			paymentNotificationService.updateStatus(notificationDto, NotificationStatus.FAILURE);
			log.info("Successfully processed failure status for orderId: {}", notificationDto.getOrderId());
		} catch (Exception e) {
			log.error("Failed to process failure status for orderId: {}", notificationDto.getOrderId(), e);
			// 실패 처리의 실패는 로깅만 하고 넘어감
		}
	}
}