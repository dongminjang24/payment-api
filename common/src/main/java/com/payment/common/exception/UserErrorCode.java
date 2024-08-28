package com.payment.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserErrorCode implements ErrorCode {

	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."),
	DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다."),
	WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다.");

	private final String name = name();
	private final HttpStatus httpStatus;
	private final String detail;

}
