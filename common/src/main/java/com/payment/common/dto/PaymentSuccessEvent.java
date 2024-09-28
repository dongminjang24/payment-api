package com.payment.common.dto;

import lombok.Getter;

@Getter
public record PaymentSuccessEvent(String paymentKey, String orderId, Long amount, String email) {
}