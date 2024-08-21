package com.payment.paymentapi.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.payment.common.model.dto.PaymentDto;
import com.payment.common.model.dto.PaymentFailDto;
import com.payment.common.model.dto.PaymentRequestDto;
import com.payment.common.model.dto.PaymentSuccessDto;
import com.payment.paymentapi.config.TossPaymentConfig;
import com.payment.paymentapi.service.TossPaymentServiceImpl;
import com.payment.common.response.SingleResponse;

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
		PaymentSuccessDto paymentSuccessDto = paymentService.paymentSuccess(paymentKey, orderId, amount);
		return ResponseEntity.ok().body(new SingleResponse<>(paymentSuccessDto));
	}

	@GetMapping("/toss/fail")
	public ResponseEntity tossPaymentFail(
		@RequestParam String code,
		@RequestParam String message,
		@RequestParam String orderId) {
		paymentService.paymentFail(code,message, orderId);

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
		@RequestParam String email,
		@RequestParam String paymentKey,
		@RequestParam String cancelReason) {

		return ResponseEntity.ok().body(new SingleResponse<>(
			paymentService
				.cancelPaymentPoint(email,paymentKey,cancelReason)));

	}

	// http://localhost:8080/history?email=user@example.com&page=0&size=10&sort=createdAt,desc

	@GetMapping("/history")
	public ResponseEntity tossPaymentCancelPoint(
		@RequestParam String email,
		@PageableDefault(size = 10, sort = "paymentId", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok().body(new SingleResponse<>(
			paymentService.findAllChargingHistories(email,pageable)
		));
	}



}