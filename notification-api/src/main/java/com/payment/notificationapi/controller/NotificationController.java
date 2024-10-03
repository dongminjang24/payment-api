package com.payment.notificationapi.controller;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.payment.common.dto.NotificationDto;
import com.payment.notificationapi.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

	private final NotificationService notificationService;

	// @GetMapping
	// public ResponseEntity<?> getNotification(@RequestParam String orderId) {
	// 	List<NotificationDto> notification = notificationService.getNotification(orderId);
	// 	return ResponseEntity.ok(new CommonResponse<>(notification));
	// }

	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe() {
		return notificationService.subscribe();
	}

	@PostMapping("/send")
	public void sendNotification(@RequestBody NotificationDto notificationDto) {
		notificationService.processBatchNotification(notificationDto);
	}
}
