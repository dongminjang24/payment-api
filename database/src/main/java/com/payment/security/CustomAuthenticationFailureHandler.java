package com.payment.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.common.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {


	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception) throws IOException {

		String errMsg = "아이디 및 비밀번호가 일치하지 않습니다.";

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		if(exception instanceof BadCredentialsException) {
			errMsg =  "아이디 및 비밀번호가 일치하지 않습니다.";
		} else if(exception instanceof DisabledException) {
			errMsg = "계정이 비활성화되어 있습니다.";
		} else if(exception instanceof CredentialsExpiredException) {
			errMsg = "비밀번호가 만료되었습니다.";
		}

		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), errMsg);

		objectMapper.writeValue(response.getWriter(), errorResponse);
	}
}
