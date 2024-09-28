package com.payment.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public record PaymentCancelEvent(String paymentKey, String orderId, String cancelReason, String email,
								 Long canceledAmount) {
}
