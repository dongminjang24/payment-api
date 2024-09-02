package com.payment.common.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponseDto {
	private Long userId;
	private static final String DETAIL_MESSAGE = "로그인이 완료되었습니다.";

	public String getDetailMessage() {
		return DETAIL_MESSAGE;
	}
}
