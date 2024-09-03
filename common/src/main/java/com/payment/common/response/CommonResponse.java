package com.payment.common.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse<T> {
	private int status = 200;
	private String message = "SUCCESS";
	private T data;

	public CommonResponse(T data) {
		this.data = data;
	}
}

