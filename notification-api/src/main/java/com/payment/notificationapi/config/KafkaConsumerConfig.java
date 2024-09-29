package com.payment.notificationapi.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.payment.common.dto.NotificationDto;

@Configuration
public class KafkaConsumerConfig {

	@Bean
	public ConsumerFactory<String, NotificationDto> consumerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "group_1");
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.payment.common.dto");

		return new DefaultKafkaConsumerFactory<>(config,
			new StringDeserializer(),
			new JsonDeserializer<>(NotificationDto.class, false));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, NotificationDto> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, NotificationDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}
}