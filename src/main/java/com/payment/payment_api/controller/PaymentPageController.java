package com.payment.payment_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;  // 올바른 Model 클래스를

import com.payment.payment_api.config.TossPaymentConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentPageController {

	private final TossPaymentConfig tossPaymentConfig;


	@GetMapping("/payment")
	public String showPaymentPage(Model model) {
		String clientKey = tossPaymentConfig.getClientApiKey();
		log.info("Controller: Client Key = {}", clientKey);

		model.addAttribute("clientKey", tossPaymentConfig.getClientApiKey());
		model.addAttribute("totalAmount", 58500);
		model.addAttribute("successUrl", tossPaymentConfig.getSuccessUrl());
		model.addAttribute("failUrl", tossPaymentConfig.getFailUrl());
		return "payment";
	}

	@GetMapping("/cancel-payment")
	public String showCancelPaymentPage() {
		return "cancel-payment";
	}

}
