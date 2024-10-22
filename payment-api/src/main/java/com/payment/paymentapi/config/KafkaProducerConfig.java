package com.payment.paymentapi.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.util.HashMap;
import java.util.Map;

import com.payment.common.config.properties.KafkaProperties;
import com.payment.common.dto.NotificationDto;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

	private final KafkaProperties kafkaProperties;

	@Bean
	public ProducerFactory<String, NotificationDto> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		// 신뢰성 관련 설정
		config.put(ProducerConfig.RETRIES_CONFIG, 3);  // 재시도 횟수
		config.put(ProducerConfig.ACKS_CONFIG, "all");  // 모든 복제본 확인
		config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);  // 중복 전송 방지

		// 성능 관련 설정
		config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);  // 배치 크기
		config.put(ProducerConfig.LINGER_MS_CONFIG, 1);  // 배치 지연 시간
		config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);  // 버퍼 메모리

		// 타임아웃 설정
		config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);  // 전송 타임아웃
		config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);  // 요청 타임아웃

		// 재시도 관련 설정
		config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);  // 재시도 간격

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, NotificationDto> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}