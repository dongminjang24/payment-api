package com.payment.common.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
	public Map<String, RestTemplate> restTemplates(RestTemplateBuilder builder) {
		Map<String, RestTemplate> templates = new HashMap<>();

		restTemplateProperties.getClients().forEach((clientName, properties) -> {
			RestTemplate template = builder
				.setConnectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
				.setReadTimeout(Duration.ofMillis(properties.getReadTimeout()))
				.build();
			templates.put(clientName, template);
		});

		return templates;
	}
}
