package com.payment.common.response.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentCancelResponse {
	private String orderId;
	private Long totalAmount;
}