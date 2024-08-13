package com.payment.payment_api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse {
	private int code;
	private String message;

	public static final int SUCCESS_CODE = 200;
	public static final String SUCCESS_MESSAGE = "SUCCESS";

	public CommonResponse() {
		this(SUCCESS_CODE, SUCCESS_MESSAGE);
	}

	public CommonResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}
}