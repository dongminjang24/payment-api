package com.payment.notifiationapi.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.common.dto.NotificationDto;
import com.payment.common.exception.CustomException;
import com.payment.common.exception.ErrorCode;
import com.payment.model.entity.Notification;
import com.payment.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final NotificationRepository notificationRepository;

	public List<NotificationDto> getNotification(String orderId) {
		List<Notification> byOrderId = notificationRepository.findByOrderId(orderId);
		if (!byOrderId.isEmpty()) {
			return byOrderId.stream()
				.map(
					notification -> NotificationDto.builder()
						.orderId(notification.getOrderId())
						.recipient(notification.getRecipient())
						.status(notification.getStatus())
						.message(notification.getMessage())
						.build()
				)
				.toList();
		} else {
			throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
		}

	}
}
