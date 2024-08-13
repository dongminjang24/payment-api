package com.payment.payment_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class TossPaymentConfig {

	@Value("${payment.toss.client_api_key}")
	private String clientApiKey;

	@Value("${payment.toss.secret_api_key}")
	private String secretKey;

	@Value("${payment.toss.success_url}")
	private String successUrl;

	@Value("${payment.toss.fail_url}")
	private String failUrl;

	public String getURL() {
		return URL;
	}
	// 변경 후
	public static final String URL = "https://api.tosspayments.com/v1/payments/";
	// 변경 전
	// public static final String URL = "https://api.tosspayments.com/v1/payments/";
}
