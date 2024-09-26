package com.payment.notifiationapi.controller;




import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.common.dto.NotificationDto;
import com.payment.common.response.CommonResponse;
import com.payment.notifiationapi.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<?> getNotification(@RequestParam String orderId) {
		List<NotificationDto> notification = notificationService.getNotification(orderId);
		return ResponseEntity.ok(new CommonResponse<>(notification));
	}
}
