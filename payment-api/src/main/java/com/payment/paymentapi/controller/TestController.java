package com.payment.paymentapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {


	@GetMapping("/sse-test")
	public String showTestNotification() {
		return "sse-test";
	}
}