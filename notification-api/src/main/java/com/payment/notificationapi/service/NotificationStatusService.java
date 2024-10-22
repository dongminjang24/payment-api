package com.payment.notificationapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.common.dto.NotificationDto;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.common.exception.CustomException;
import com.payment.common.exception.ErrorCode;
import com.payment.model.entity.Notification;
import com.payment.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationStatusService {
	private final NotificationRepository notificationRepository;

	@Transactional
	public void saveStatus(NotificationDto notificationDto, NotificationStatus status) {
		try {
			notificationRepository.findByOrderId(notificationDto.getOrderId())
				.ifPresentOrElse(
					existingNotification -> updateExistingNotification(existingNotification, status),
					() -> createNewNotification(notificationDto, status)
				);
		} catch (Exception e) {
			log.error("Failed to save notification status", e);
			throw new CustomException(ErrorCode.NOTIFICATION_STATUS_UPDATE_FAILED);
		}
	}

	private void updateExistingNotification(Notification notification, NotificationStatus status) {
		notification.updateStatus(status);
		notificationRepository.save(notification);
		log.debug("Updated notification status to {} for orderId: {}",
			status, notification.getOrderId());
	}

	private void createNewNotification(NotificationDto notificationDto, NotificationStatus status) {
		Notification newNotification = Notification.builder()
			.orderId(notificationDto.getOrderId())
			.message(notificationDto.getMessage())
			.recipient(notificationDto.getSender())
			.status(status)
			.build();
		notificationRepository.save(newNotification);
		log.debug("Created new notification with status {} for orderId: {}",
			status, notificationDto.getOrderId());
	}
}