package com.payment.notificationapi.service;


import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.payment.common.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	public SseEmitter subscribe() {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		emitters.add(emitter);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		return emitter;
	}

	@KafkaListener(topics = "payment-notifications", groupId = "notification-group")
	public void listenNotifications(NotificationDto notificationDto) {
		log.info("Received notification: {}", notificationDto);
		sendNotification(notificationDto);
	}

	public void sendNotification(NotificationDto notificationDto) {
		emitters.forEach(emitter -> {
			try {
				emitter.send(notificationDto);
			} catch (IOException e) {
				emitters.remove(emitter);
			}
		});
	}

}
