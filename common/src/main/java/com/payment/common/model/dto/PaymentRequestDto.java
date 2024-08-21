package com.payment.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {
	private String payType;
	private Long amount;
	private String orderName;
	private String orderId;
	private String customerEmail;
	private String customerName;
	private String successUrl;
	private String failUrl;

	private String failReason;
	private boolean cancelYN;
	private String cancelReason;
	private String createdAt;

}
