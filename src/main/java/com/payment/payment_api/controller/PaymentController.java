package com.payment.payment_api.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.payment.payment_api.config.TossPaymentConfig;
import com.payment.payment_api.model.dto.PaymentFailDto;
import com.payment.payment_api.model.dto.PaymentRequestDto;
import com.payment.payment_api.model.dto.PaymentSuccessDto;
import com.payment.payment_api.response.SingleResponse;
import com.payment.payment_api.service.TossPaymentServiceImpl;
import com.payment.payment_api.model.dto.PaymentDto;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
	private final TossPaymentServiceImpl paymentService;
	private final TossPaymentConfig tossPaymentConfig;

	@PostMapping("/toss")
	public ResponseEntity<SingleResponse<PaymentRequestDto>> requestTossPayment(
		@RequestBody @Valid PaymentDto paymentReqDto) {
		System.out.println("requestTossPayment ===>");

		PaymentRequestDto paymentRequestDto = paymentService.requestPayment(paymentReqDto.toEntity(), paymentReqDto.getEmail()).toPaymentResDto();
		paymentRequestDto.setSuccessUrl(tossPaymentConfig.getSuccessUrl());
		paymentRequestDto.setFailUrl(tossPaymentConfig.getFailUrl());
		return ResponseEntity.ok().body(new SingleResponse<>(paymentRequestDto));
	}

	@GetMapping("/toss/success")
	public ResponseEntity<SingleResponse<PaymentSuccessDto>> getPaymentSuccess(
		@RequestParam String orderId,
		@RequestParam String paymentKey,
		@RequestParam Long amount
		) {
		System.out.println("orderId = " + orderId);
		System.out.println("paymentKey = " + paymentKey);
		System.out.println("amount = " + amount);
		PaymentSuccessDto paymentSuccessDto = paymentService.tossPaymentSuccess(paymentKey, orderId, amount);
		return ResponseEntity.ok().body(new SingleResponse<>(paymentSuccessDto));
	}

	@GetMapping("/toss/fail")
	public ResponseEntity tossPaymentFail(
		@RequestParam String code,
		@RequestParam String message,
		@RequestParam String orderId) {
		paymentService.tossPaymentFail(code,message, orderId);

		return ResponseEntity.ok().body(new SingleResponse<>(
			PaymentFailDto.builder()
				.errorCode(code)
				.errorMessage(message)
				.orderId(orderId)
				.build()
		));

	}

	@GetMapping("/toss/cancel/point")
	public ResponseEntity tossPaymentCancelPoint(
		String email,
		@RequestParam String paymentKey,
		@RequestParam String cancelReason) {

		return ResponseEntity.ok().body(new SingleResponse<>(
			paymentService
				.cancelPaymentPoint(email,paymentKey,cancelReason)));

	}

	// http://localhost:8080/history?email=user@example.com&page=0&size=10&sort=createdAt,desc

	@GetMapping("/history")
	public ResponseEntity tossPaymentCancelPoint(
		String email,
		Pageable pageable){
		return ResponseEntity.ok().body(new SingleResponse<>(
			paymentService.findAllChargingHistories(email,pageable)
		));
	}



}