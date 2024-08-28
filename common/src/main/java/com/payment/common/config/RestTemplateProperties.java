package com.payment.common.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "rest.template")
@Getter
@Setter
public class RestTemplateProperties {
	private int connectTimeout;
	private int readTimeout;
}