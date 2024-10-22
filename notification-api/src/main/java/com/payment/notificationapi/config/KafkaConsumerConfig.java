package com.payment.notificationapi.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.fasterxml.jackson.core.JsonParseException;
import com.payment.common.config.properties.KafkaProperties;
import com.payment.common.dto.NotificationDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

	private final KafkaProperties kafkaProperties;
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;  // DLQ 발행을 위해 필요

	@Bean
	public ConsumerFactory<String, NotificationDto> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.payment.common.dto");
		config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 오토 커밋 안되게 설정
		return new DefaultKafkaConsumerFactory<>(config,
			new StringDeserializer(),
			new JsonDeserializer<>(NotificationDto.class, false));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, NotificationDto> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, NotificationDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		/*
		* 메시지를 배치(batch)로 처리하도록 설정 ->
		* 따로따로 설정하는 것이 아니라 여러 메시지를 한번에 모아서 처리
		* */
		factory.setBatchListener(true);
		factory.setConsumerFactory(consumerFactory());
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);  // 수동 승인 모드 설정

		// DLQ 설정
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
			(record, ex) -> {
				log.error("Moving message to DLQ. Topic: {}, Exception: {}",
					record.topic(), ex.getMessage());

				return new TopicPartition(record.topic() + ".DLT", record.partition());
			});

		// 에러 핸들러 설정
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(
			recoverer,
			new FixedBackOff(1000L, 3L)  // 1초 간격으로 3번 재시도
		);

		// 특정 예외는 바로 DLQ로
		errorHandler.addNotRetryableExceptions(
			IllegalArgumentException.class,
			JsonParseException.class,
			SecurityException.class
		);

		factory.setCommonErrorHandler(errorHandler);
		return factory;
	}



}