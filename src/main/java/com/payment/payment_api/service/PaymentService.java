package com.payment.payment_api.service;


import java.util.List;

import com.payment.payment_api.model.dto.PaymentDto;
import com.payment.payment_api.model.dto.PaymentRequestDto;
import com.payment.payment_api.model.entity.Payment;

public interface PaymentService {

	/**
	 * Toss Payment 요청을 처리합니다.
	 */
	Payment requestPayment(Payment payment, String userEmail);

	/**
	 * 결제 정보를 조회합니다.
	 */
	// Payment getPayment(Long paymentId);
	//
	// /**
	//  * 결제를 취소합니다.
	//  */
	// Payment cancelPayment(Long paymentId, String cancelReason);
	//
	// /**
	//  * 사용자의 모든 결제 내역을 조회합니다.
	//  */
	// List<Payment> getUserPayments(String userEmail);
	//
	// /**
	//  * 결제 상태를 업데이트합니다.
	//  */
	// Payment updatePaymentStatus(Long paymentId, boolean paySuccessYN, String failReason);


}