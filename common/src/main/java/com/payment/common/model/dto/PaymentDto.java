package com.payment.common.model.dto;

import com.payment.common.model.entity.Payment;
import com.payment.common.model.enum_type.PayType;

import jakarta.validation.constraints.NotBlank;
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
public class PaymentDto {

	@NotBlank
	private PayType payType;

	@NotBlank
	private Long amount;

	@NotBlank
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
