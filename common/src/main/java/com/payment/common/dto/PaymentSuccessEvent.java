package com.payment.common.dto;


public record PaymentSuccessEvent(String paymentKey, String orderId, Long amount, String email) {
}