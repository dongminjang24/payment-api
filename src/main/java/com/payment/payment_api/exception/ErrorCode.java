package com.payment.payment_api.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

	INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 올바르지 않습니다. 다시 확인해 주세요. "),
	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."),
	PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청하신 결제 정보를 찾을 수 없습니다. 입력하신 정보를 다시 확인해 주세요."),
	PAYMENT_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "결제 취소에 실패했습니다. 다시 시도해 주세요."),
	PAYMENT_AMOUNT_EXP(HttpStatus.INTERNAL_SERVER_ERROR, "요청하신 금액과 결제 금액이 다릅니다. 금액을 다시 확인해 주세요."),
	ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "거래가 이미 완료되었습니다."),
	PAYMENT_CONFIRMATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "거래가 실패하였습니다"),
	UNEXPECTED_PAYMENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "거래에서 예상치 못한 오류가 발생하였습니다."),
	PAYMENT_NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "거래에 충분한 포인트가 없습니다.");

	private final HttpStatus httpStatus;
	private final String detail;

}
