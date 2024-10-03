package com.payment.notificationapi.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.payment.common.dto.NotificationDto;
import com.payment.model.entity.Payment;

@Component
public class NotificationItemProcessor implements ItemProcessor<Payment, NotificationDto> {

	@Override
	public NotificationDto process(Payment payment) {
		return NotificationDto.builder()
			.orderId(payment.getOrderId())
			.message("Payment amount : " + payment.getAmount() + " Payment createdAt : " + payment.getCreatedAt())
			.sender("BatchJob")
			.build();
	}
}