package com.payment.paymentapi.dto;

import java.util.List;

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
public class PaymentSliceDto {
	private List<PaymentDto> content;
	private boolean hasNext;
	private int number;
	private int size;
}
