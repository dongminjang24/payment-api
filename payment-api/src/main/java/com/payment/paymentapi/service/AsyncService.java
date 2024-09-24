package com.payment.paymentapi.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

	@Async("threadPoolTaskExecutor")
	public void testAsync(String message) {
		// 비동기로 처리될 작업
		for (int i = 1; i <= 3; i++) {
			System.out.println(message + " 비동기: " + i);
		}
	}
}