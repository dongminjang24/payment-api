package com.payment.common.dto;

public record PaymentCancelEvent(String paymentKey, String orderId, String cancelReason, String email,
								 Long canceledAmount) {
}
