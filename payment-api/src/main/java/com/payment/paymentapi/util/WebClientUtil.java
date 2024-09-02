// package com.payment.paymentapi.util;
//
// import org.springframework.http.HttpMethod;
// import org.springframework.stereotype.Component;
//
// import com.payment.paymentapi.config.WebClientConfig;
//
// import lombok.RequiredArgsConstructor;
// import reactor.core.publisher.Mono;
//
// @Component
// @RequiredArgsConstructor
// public class WebClientUtil {
//
// 	private final WebClientConfig webClientConfig;
//
// 	public <T> T get(String url, Class<T> responseDtoClass) {
// 		return webClientConfig.webClient().method(HttpMethod.GET)
// 			.uri(url)
// 			.retrieve()
// 			.onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(InternalServerErrorCode.EXCEPTION))
// 			.onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(InternalServerException.EXCEPTION))
// 			.bodyToMono(responseDtoClass)
// 			.block();
// 	}
//
// 	public <T, V> T post(String url, V requestDto, Class<T> responseDtoClass) {
// 		return webClientConfig.webClient().method(HttpMethod.POST)
// 			.uri(url)
// 			.bodyValue(requestDto)
// 			.retrieve()
// 			.onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(InternalServerException.EXCEPTION))
// 			.onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(InternalServerException.EXCEPTION))
// 			.bodyToMono(responseDtoClass)
// 			.block();
// 	}
// }
