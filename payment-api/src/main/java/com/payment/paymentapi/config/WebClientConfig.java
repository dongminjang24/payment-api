package com.payment.paymentapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paymentapi.config.properties.WebClientProperties;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class WebClientConfig {

	private final WebClientProperties webClientProperties;
	private final ObjectMapper objectMapper;
	private static final int MAX_LOG_LENGTH = 1000;

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
				.filter(logRequest())
				.filter(logResponse())
				.uriBuilderFactory(factory)
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();

			webClients.put(clientName, webClient);
		});

		return webClients;
	}

	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
			if (log.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder("Request: \n");
				sb.append("URI: ").append(clientRequest.url()).append("\n");
				sb.append("Method: ").append(clientRequest.method()).append("\n");
				sb.append("Headers: ").append(maskSensitiveInfo(clientRequest.headers())).append("\n");

				// Log the body only for specific content types
				if (clientRequest.body() != null &&
					clientRequest.headers().getContentType() != null &&
					clientRequest.headers().getContentType().includes(MediaType.valueOf("application/json"))) {
					String bodyString = maskSensitiveInfo(clientRequest.body().toString());
					sb.append("Body: ").append(truncateLog(bodyString)).append("\n"); // Truncate log
				}

				log.debug(sb.toString());
			}
			return Mono.just(clientRequest);
		});
	}

	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			if (log.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder("Response: \n");
				sb.append("Status: ").append(clientResponse.statusCode()).append("\n");
				sb.append("Headers: ").append(maskSensitiveInfo(clientResponse.headers().asHttpHeaders())).append("\n");

				// Log response body only for specific content types
				if (clientResponse.headers().contentType().isPresent() &&
					clientResponse.headers().contentType().get().includes(MediaType.valueOf("application/json"))) {
					return clientResponse.bodyToMono(String.class)
						.flatMap(body -> {
							String truncatedBody = truncateLog(maskSensitiveInfo(body)); // Truncate log
							sb.append("Body: ").append(truncatedBody).append("\n");
							log.debug(sb.toString());
							return Mono.just(clientResponse.mutate().body(body).build());
						});
				}

				log.debug(sb.toString());
			}
			return Mono.just(clientResponse);
		});
	}

	// 로그 길이 제한을 위한 헬퍼 메서드
	private String truncateLog(String log) {
		if (log.length() > MAX_LOG_LENGTH) {
			return log.substring(0, MAX_LOG_LENGTH) + "... [TRUNCATED]";
		}
		return log;
	}

	private String maskSensitiveInfo(Object obj) {
		try {
			String json = objectMapper.writeValueAsString(obj);
			// Implement your masking logic here. For example:
			json = json.replaceAll("(\"password\"\\s*:\\s*\")[^\"]*\"", "$1*****\"");
			return json;
		} catch (Exception e) {
			log.warn("Failed to mask sensitive information", e);
			return "[MASKING FAILED]";
		}
	}
}