package com.payment.notificationapi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.common.dto.NotificationDto;
import com.payment.common.exception.CustomException;
import com.payment.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProcessor {
	private final NotificationOrchestrationService notificationOrchestrationService;
	private final SendNotification sendNotification;

	@Transactional
	public void processNotification(NotificationDto notificationDto) {
		try {
			// 1. 알림 상태를 PENDING으로 저장
			notificationOrchestrationService.processInitialStatus(notificationDto);

			// 2. SSE 전송
			boolean sent = sendNotification.sendNotification(notificationDto);
			if (!sent) {
				notificationOrchestrationService.processFailureStatus(notificationDto);
				throw new CustomException(ErrorCode.NOTIFICATION_SEND_FAILED);
			}

			// 3. 성공 상태로 업데이트
			notificationOrchestrationService.processSuccessStatus(notificationDto);

		} catch (CustomException e) {
			log.error("Failed to process notification for orderId: {}", notificationDto.getOrderId(), e);
			notificationOrchestrationService.processFailureStatus(notificationDto);
			throw e;
		} catch (Exception e) {
			log.error("Unexpected error processing notification for orderId: {}", notificationDto.getOrderId(), e);
			notificationOrchestrationService.processFailureStatus(notificationDto);
			throw new CustomException(ErrorCode.NOTIFICATION_PROCESSING_FAILED);
		}
	}
}