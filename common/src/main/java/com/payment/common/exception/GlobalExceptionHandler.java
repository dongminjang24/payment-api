package com.payment.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<?> handleCustomException(CustomException e) {
		HttpStatus status = e.getErrorCode().getHttpStatus(); // 상태 코드를 ErrorCode에서 추출
		Map<String, Object> body = new HashMap<>();
		body.put("code", e.getErrorCode()); // Enum 이름을 오류 코드로 사용
		body.put("message", e.getErrorCode().getDetail());
		body.put("status", status.value());
		return new ResponseEntity<>(body, status);
	}
}
