package com.payment.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "spring.batch")
@Getter
@Setter
public class BatchProperties {
	private String createdAt;
}
