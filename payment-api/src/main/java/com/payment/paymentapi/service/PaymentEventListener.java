package com.payment.paymentapi.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.payment.common.dto.NotificationDto;
import com.payment.common.dto.PaymentCancelEvent;
import com.payment.common.dto.PaymentSuccessEvent;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.notificationapi.service.RedisPubService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {
	private final RedisPubService redisPubService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
		NotificationDto messageDto = NotificationDto.builder()
			.orderId(event.orderId())
			.message("payment success")
			.sender(event.email())
			.status(NotificationStatus.SUCCESS)
			.recipient("payment")
			.build();

		redisPubService.pubMsgChannel("payment", messageDto);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePaymentCancelEvent(PaymentCancelEvent event) {
		NotificationDto messageDto = NotificationDto.builder()
			.message("payment cancel")
			.status(NotificationStatus.CANCEL)
			.sender(event.email())
			.orderId(event.orderId())
			.recipient("cancel")
			.build();

		redisPubService.pubMsgChannel("cancel", messageDto);
	}
}