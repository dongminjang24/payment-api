package com.payment.notificationapi.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.payment.common.dto.NotificationDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDLQService {

	private final NotificationDLQProcessor notificationDLQProcessor;

	@KafkaListener(topics = "payment-notifications.DLT", groupId = "notification-dlq-group")
	public void processDLQMessage(NotificationDto notificationDto, Acknowledgment acknowledgment) {
		log.error("Processing message from DLQ: {}", notificationDto);
		try {
			notificationDLQProcessor.processFailedNotification(notificationDto);
			acknowledgment.acknowledge();
		} catch (Exception e) {
			log.error("Error processing DLQ message", e);
			acknowledgment.acknowledge();
		}
	}


}
