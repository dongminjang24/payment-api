package com.payment.common.model.dto;

import com.payment.common.model.entity.Payment;
import com.payment.common.model.enum_type.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

	@NonNull
	private PayType payType;

	@NonNull
	private Long amount;

	@NonNull
	private String orderName;

	private String yourSuccessUrl;

	private String yourFailUrl;

	private String email;

	public Payment toEntity() {
		return Payment.builder()
			.payType(payType)
			.amount(amount)
			.orderName(orderName)
			.orderId(null)
			.paySuccessYN(false)
			.build();
	}
}
