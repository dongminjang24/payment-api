package com.payment.paymentapi.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.payment.paymentapi.config.properties.WebClientProperties;

import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;


@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

	private final WebClientProperties webClientProperties;

	@Bean
	public Map<String, WebClient> webClients() {
		Map<String, WebClient> webClients = new HashMap<>();

		webClientProperties.getClients().forEach((clientName, properties) -> {
			DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
			factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

			HttpClient httpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
				.responseTimeout(Duration.ofMillis(properties.getReadTimeout()));

			WebClient webClient = WebClient.builder()
				.uriBuilderFactory(factory)
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();

			//TODO: 로깅 설정 추가
			//로그를 모아서 공통화시킨다.

			webClients.put(clientName, webClient);
		});

		return webClients;
	}
}

