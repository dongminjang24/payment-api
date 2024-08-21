package com.payment.common.service;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.payment.common.model.dto.PaymentSuccessDto;
import com.payment.common.model.entity.Payment;

public interface PaymentService {

	Payment requestPayment(Payment payment, String userEmail);

	PaymentSuccessDto paymentSuccess(String paymentKey, String orderId, Long amount);

	Payment verifyPayment(String orderId, Long amount);

	PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount);

	void paymentFail(String code, String message, String orderId);

	Map<String, Object> cancelPaymentPoint(String email, String paymentKey, String cancelReason);

	Slice<Payment> findAllChargingHistories(String email, Pageable pageable);

	// 필요에 따라 다음 메서드도 추가할 수 있습니다:
	// Map<String, Object> tossPaymentCancel(String paymentKey, String cancelReason);
}
