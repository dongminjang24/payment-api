package com.payment.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler  extends ResponseEntityExceptionHandler {


	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
		ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
		return new ResponseEntity<>(errorResponse, e.getErrorCode().getHttpStatus());
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
		ErrorResponse errorResponse = new ErrorResponse(
			HttpStatus.INTERNAL_SERVER_ERROR.name(),
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			"예상치 못한 오류가 발생했습니다."
		);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
		Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		String code = extractStatusCode(statusCode);
		ErrorResponse errorResponse = new ErrorResponse(
			code,
			statusCode.value(),
			"서버에서 오류가 발생했습니다."
		);
		return new ResponseEntity<>(errorResponse, headers, statusCode);
	}

	private String extractStatusCode(HttpStatusCode statusCode) {
		if (statusCode instanceof HttpStatus) {
			return ((HttpStatus) statusCode).name();
		}
		// HttpStatus enum에 없는 상태 코드의 경우
		return "STATUS_" + statusCode.value();
	}
}
