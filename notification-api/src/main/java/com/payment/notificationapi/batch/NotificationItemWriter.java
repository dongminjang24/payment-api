package com.payment.notificationapi.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.payment.common.dto.NotificationDto;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.model.entity.Payment;
import com.payment.notificationapi.service.NotificationService;
import com.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationItemWriter implements ItemWriter<NotificationDto> {

	private final NotificationService notificationService;

	@Override
	@Transactional
	public void write(Chunk<? extends NotificationDto> chunk) throws Exception {
		for (NotificationDto notification : chunk) {
				notificationService.processBatchNotification(notification);
		}
	}



}
