package com.payment.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@Setter
public class KafkaProperties {
	private String bootstrapServers;
}
