package com.payment.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "cache")
@Getter
@Setter
public class CacheProperties {
	private long ttl;
	private String evictionStrategy;
}
