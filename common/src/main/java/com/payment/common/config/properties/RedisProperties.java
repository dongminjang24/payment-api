package com.payment.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "spring.data.redis")
@Getter
@Setter
public class RedisProperties {
	private String host;
	private int port;
}