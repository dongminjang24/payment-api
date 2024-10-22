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
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationProcessor notificationProcessor;


	// 기존 transaction 어노테이션 제거
	// KAFKA_CREATE_TOPICS: "payment-notifications:1:1"
	@KafkaListener(topics = "payment-notifications", groupId = "notification-group", concurrency = "1")
	public void listenNotifications(NotificationDto notificationDto, Acknowledgment acknowledgment) {
		log.info("Received notification: {}", notificationDto);
		try {
			notificationProcessor.processNotification(notificationDto);
			acknowledgment.acknowledge();
		} catch (Exception e) {
			log.error("Error processing notification", e);
			acknowledgment.acknowledge();
			throw e;
		}
	}


	// 배치 처리를 위한 메서드
	public void processBatchNotification(NotificationDto notificationDto) {
		notificationProcessor.processNotification(notificationDto);
	}
}