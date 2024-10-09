package com.payment.notificationapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

	private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
	private final NotificationRepository notificationRepository;
	private final PaymentRepository paymentRepository;

	public SseEmitter subscribe(String memberEmail) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.put(memberEmail, emitter);
		emitter.onCompletion(() -> emitters.remove(memberEmail));
		emitter.onTimeout(() -> emitters.remove(memberEmail));
		return emitter;
	}


	// 기존 transaction 어노테이션 제거
	@KafkaListener(topics = "payment-notifications", groupId = "notification-group")
	public void listenNotifications(NotificationDto notificationDto, Acknowledgment acknowledgment) {
		log.info("Received notification: {}", notificationDto);
		try {
			processNotification(notificationDto);
			acknowledgment.acknowledge();  // 메시지 처리 후 오프셋 커밋
		} catch (Exception e) {
			log.error("Error processing notification", e);
			// 여기서는 acknowledge()를 호출하지 않습니다. 실패한 경우 재처리를 위해
		}
	}

	@Transactional
	public void processNotification(NotificationDto notificationDto) {
		try {
			// 1. 알림 상태를 PENDING으로 저장
			saveNotification(notificationDto, NotificationStatus.PENDING);
			updatePaymentNotiStatus(notificationDto, NotificationStatus.PENDING);

			// 2. SSE 전송 (트랜잭션 외부에서 실행)
			boolean sent = sendNotification(notificationDto);

			// 3. 결과에 따라 상태 업데이트
			NotificationStatus finalStatus = sent ? NotificationStatus.SUCCESS : NotificationStatus.FAILURE;
			saveNotification(notificationDto, finalStatus);
			updatePaymentNotiStatus(notificationDto, finalStatus);

		} catch (Exception e) {
			log.error("Failed to process notification for orderId: {}", notificationDto.getOrderId(), e);
			saveNotification(notificationDto, NotificationStatus.FAILURE);
			updatePaymentNotiStatus(notificationDto, NotificationStatus.FAILURE);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean sendNotification(NotificationDto notificationDto) {
		boolean sent = false;
		SseEmitter emitter = emitters.get(notificationDto.getSender());
		if (emitter != null) {
			try {
				emitter.send(notificationDto);
				sent = true;
			} catch (IOException e) {
				emitters.remove(notificationDto.getSender());
				log.warn("Failed to send notification to recipient: {}, removing emitter", notificationDto.getSender(), e);
			}
		}
		return sent;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveNotification(NotificationDto notificationDto, NotificationStatus status) {
		notificationRepository.findByOrderId(notificationDto.getOrderId()).ifPresentOrElse(
			existingNotification -> {
				existingNotification.updateStatus(status);
				notificationRepository.save(existingNotification);
			},
			() -> {
				notificationRepository.save(Notification.builder()
					.orderId(notificationDto.getOrderId())
					.message(notificationDto.getMessage())
					.recipient(notificationDto.getSender())
					.status(status)
					.build());
			}
		);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updatePaymentNotiStatus(NotificationDto notificationDto, NotificationStatus status) {
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