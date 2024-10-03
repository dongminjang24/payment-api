package com.payment.notificationapi.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.payment.notificationapi.batch.NotificationItemProcessor;
import com.payment.common.dto.NotificationDto;
import com.payment.model.entity.Payment;
import com.payment.notificationapi.batch.NotificationItemWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final JpaPagingItemReader<Payment> notificationItemReader;
	private final NotificationItemProcessor notificationItemProcessor;
	private final NotificationItemWriter notificationItemWriter;
	@Bean
	public Job notificationJob(JobRepository jobRepository, Step notificationStep) {
		return new JobBuilder("notificationJob", jobRepository)
			.start(notificationStep)
			.build();
	}

	@Bean
	public Step notificationStep(JobRepository jobRepository,
		PlatformTransactionManager transactionManager) {
		return new StepBuilder("notificationStep", jobRepository)
			.<Payment, NotificationDto>chunk(10, transactionManager)
			.reader(notificationItemReader)
			.processor(notificationItemProcessor)
			.writer(notificationItemWriter)
			.build();
	}
}