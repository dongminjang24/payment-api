package com.payment.notificationapi.batch;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.stereotype.Component;

import com.payment.model.entity.Payment;

import jakarta.persistence.EntityManagerFactory;

@StepScope
@Component
public class NotificationItemReader extends JpaPagingItemReader<Payment> {
	public NotificationItemReader(EntityManagerFactory entityManagerFactory) {
		super();
		setName("notificationItemReader");
		setEntityManagerFactory(entityManagerFactory);
		setPageSize(10);
		setQueryString("SELECT p FROM Payment p WHERE p.createdAt > :createdAt AND p.notificationStatus != 'SUCCESS'");
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("createdAt", LocalDateTime.now().minusMinutes(5));
		setParameterValues(parameterValues);
	}
}