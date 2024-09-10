package com.payment.common.exception;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;



@Getter
public class ErrorResponse {
	private final String message;
	private final int status;

	@JsonIgnore
	private Map<String, String> errors;

	public ErrorResponse(ErrorCode errorCode) {
		this.message = errorCode.getDetail();
		this.status = errorCode.getHttpStatus().value();
	}

	public ErrorResponse( int status,String message) {
		this.message = message;
		this.status = status;
	}

}