package com.payment.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "monitoring.slow-query")
@Getter
@Setter
public class SlowQueryMonitoringProperties {

	private long slowQueryThreshold = 1; // 기본값은 1000ms (1초)
}
