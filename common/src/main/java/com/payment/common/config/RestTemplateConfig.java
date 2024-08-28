package com.payment.common.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

	private final RestTemplateProperties restTemplateProperties;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
			.setConnectTimeout(Duration.ofMillis(restTemplateProperties.getConnectTimeout()))
			.setReadTimeout(Duration.ofMillis(restTemplateProperties.getReadTimeout()))
			.build();
	}
}
