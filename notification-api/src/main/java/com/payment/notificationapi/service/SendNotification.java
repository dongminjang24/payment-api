package com.payment.notificationapi.service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.payment.common.dto.NotificationDto;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class SendNotification {

	private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	public SseEmitter subscribe(String memberEmail) {
		log.info("Subscribing to notifications for user: {}", memberEmail);

		// 기존 emitter가 있다면 제거
		removeEmitter(memberEmail);

		SseEmitter emitter = new SseEmitter(180_000L); // 3분 타임아웃
		emitter.onCompletion(() -> {
			log.info("SSE completed for user: {}", memberEmail);
			removeEmitter(memberEmail);
		});

		emitter.onTimeout(() -> {
			log.info("SSE timeout for user: {}", memberEmail);
			removeEmitter(memberEmail);
		});

		emitter.onError(ex -> {
			log.error("SSE error for user: {}", memberEmail, ex);
			removeEmitter(memberEmail);
		});

		emitters.put(memberEmail, emitter);

		// 연결 즉시 테스트 이벤트 전송
		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("Connected successfully!")
				.id(String.valueOf(System.currentTimeMillis())));
			log.info("Initial connection event sent to user: {}", memberEmail);
		} catch (IOException e) {
			log.error("Error sending initial test event to user: {}", memberEmail, e);
			removeEmitter(memberEmail);
			return null;
		}

		return emitter;
	}

	// 주석으로 강조하기!!!!
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public boolean sendNotification(NotificationDto notificationDto) {
		String recipient = notificationDto.getSender();
		log.debug("Attempting to send notification to user: {}", recipient);

		SseEmitter emitter = emitters.get(recipient);

		if (emitter == null) {
			log.info("No active SSE connection found for user: {}. Message will be queued or logged.", recipient);
			return true; // SSE 연결이 없어도 성공으로 처리
		}

		try {
			emitter.send(SseEmitter.event()
				.name("notification")
				.data(notificationDto)
				.id(String.valueOf(System.currentTimeMillis())));

			log.debug("Successfully sent notification to user: {}", recipient);
			return true;

		} catch (IOException e) {
			log.error("Failed to send notification to user: {}", recipient, e);
			removeEmitter(recipient);
			return false;
		} catch (Exception e) {
			log.error("Unexpected error while sending notification to user: {}", recipient, e);
			removeEmitter(recipient);
			return false;
		}
	}

	private void removeEmitter(String memberEmail) {
		SseEmitter emitter = emitters.remove(memberEmail);
		if (emitter != null) {
			try {
				emitter.complete();
			} catch (Exception e) {
				log.warn("Error while completing emitter for user: {}", memberEmail, e);
			}
		}
	}


}