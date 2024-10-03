package com.payment.notificationapi.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.payment.common.dto.NotificationDto;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.model.entity.Notification;
import com.payment.repository.NotificationRepository;
import com.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	private final NotificationRepository notificationRepository;
	private final PaymentRepository paymentRepository;

	public SseEmitter subscribe() {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.add(emitter);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		return emitter;
	}

	@Transactional
	@KafkaListener(topics = "payment-notifications", groupId = "notification-group")
	public void listenNotifications(NotificationDto notificationDto) {
		log.info("Received notification: {}", notificationDto);
		processNotification(notificationDto);
	}

	@Transactional
	public void processNotification(NotificationDto notificationDto) {
		try {
			sendNotification(notificationDto);
			saveNotification(notificationDto, NotificationStatus.SUCCESS);
			updatePaymentNotiStatus(notificationDto, NotificationStatus.SUCCESS);
		} catch (Exception e) {
			log.error("Failed to send notification for orderId: {}", notificationDto.getOrderId(), e);
			saveNotification(notificationDto, NotificationStatus.FAILURE);
			updatePaymentNotiStatus(notificationDto, NotificationStatus.FAILURE);
		}
	}

	private void sendNotification(NotificationDto notificationDto) {
		List<SseEmitter> deadEmitters = new ArrayList<>();
		emitters.forEach(emitter -> {
			try {
				emitter.send(notificationDto);
			} catch (IOException e) {
				deadEmitters.add(emitter);
			}
		});
		emitters.removeAll(deadEmitters);
	}

	private void saveNotification(NotificationDto notificationDto, NotificationStatus status) {
		notificationRepository.save(Notification.builder()
			.orderId(notificationDto.getOrderId())
			.message(notificationDto.getMessage())
			.recipient(notificationDto.getSender())
			.status(status)
			.build());
	}

	private void updatePaymentNotiStatus(NotificationDto notificationDto, NotificationStatus status) {
		String orderId = notificationDto.getOrderId();
		paymentRepository.findByOrderId(orderId)
			.ifPresentOrElse(
				payment -> {
					payment.setNotificationStatus(status);
					paymentRepository.save(payment);
				},
				() -> log.warn("Payment not found for orderId: {}", orderId)
			);
	}

	// 배치 처리를 위한 메서드
	@Transactional
	public void processBatchNotification(NotificationDto notificationDto) {
		processNotification(notificationDto);
	}
}