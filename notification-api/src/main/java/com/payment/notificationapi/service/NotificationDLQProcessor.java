package com.payment.notificationapi.service;

import org.springframework.stereotype.Service;

import com.payment.common.dto.NotificationDto;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.model.entity.Notification;
import com.payment.repository.NotificationRepository;
import com.payment.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDLQProcessor {
	private final NotificationRepository notificationRepository;
	private final PaymentRepository paymentRepository;
	// private final AlertService alertService;

	@Transactional
	public void processFailedNotification(NotificationDto notificationDto) {
		updateOrSaveFailedNotification(notificationDto);
		updatePaymentNotificationStatus(notificationDto);

		// 관리자에게 알림이 가도록 설정해도 좋을 것 같음

	}

	private void updateOrSaveFailedNotification(NotificationDto notificationDto) {
		notificationRepository.findByOrderId(notificationDto.getOrderId())
			.ifPresentOrElse(
				notification -> {
					notification.updateStatus(NotificationStatus.FAILURE);
					notificationRepository.save(notification);
				},
				() -> {
					Notification newNotification = Notification.builder()
						.orderId(notificationDto.getOrderId())
						.message(notificationDto.getMessage())
						.recipient(notificationDto.getSender())
						.status(NotificationStatus.FAILURE)
						.build();

					paymentRepository.findByOrderId(notificationDto.getOrderId())
						.ifPresent(newNotification::updatePayment);

					notificationRepository.save(newNotification);
				}
			);
	}

	private void updatePaymentNotificationStatus(NotificationDto notificationDto) {
		paymentRepository.findByOrderId(notificationDto.getOrderId())
			.ifPresent(payment -> {
				payment.setNotificationStatus(NotificationStatus.FAILURE);
				paymentRepository.save(payment);
			});
	}


}

