package com.payment.paymentapi.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.payment.common.dto.NotificationDto;
import com.payment.common.dto.PaymentCancelEvent;
import com.payment.common.dto.PaymentSuccessEvent;
import com.payment.common.enum_type.NotificationStatus;
// import com.payment.notificationapi.service.RedisPubService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
		// NotificationDto messageDto = NotificationDto.builder()
		// 	.orderId(event.orderId())
		// 	.message("payment success")
		// 	.sender(event.email())
		// 	.status(NotificationStatus.SUCCESS)
		// 	.build();

		NotificationDto messageDto = new NotificationDto(event.orderId(), "payment success", event.email(), NotificationStatus.SUCCESS);


		sendNotification(messageDto);
	}


	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePaymentCancelEvent(PaymentCancelEvent event) {
		// NotificationDto messageDto = NotificationDto.builder()
		// 	.message("payment cancel")
		// 	.status(NotificationStatus.CANCEL)
		// 	.sender(event.email())
		// 	.orderId(event.orderId())
		// 	.build();

		NotificationDto messageDto = new NotificationDto(event.orderId(), "payment cancel", event.email(), NotificationStatus.CANCEL);

		sendNotification(messageDto);
	}

	private void sendNotification(NotificationDto notificationDto) {
		try {
			kafkaTemplate.send("payment-notifications", notificationDto);
			log.info("Notification sent successfully: {}", notificationDto);
		} catch (Exception e) {
			log.error("Failed to send notification: {}", notificationDto, e);
		}
	}
}