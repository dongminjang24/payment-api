package com.payment.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.common.response.CommonResponse;
import com.payment.common.response.dto.SignInResponseDto;
import com.payment.model.entity.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException {

		Member user = (Member) authentication.getPrincipal();

		// Create session
		HttpSession session = request.getSession(true);
		session.setAttribute("MEMBER_ID", user.getId());

		SignInResponseDto result = SignInResponseDto.builder()
			.userId(user.getId())
			.build();
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		CommonResponse<SignInResponseDto> responseEntity = new CommonResponse<>(result);
		objectMapper.writeValue(response.getWriter(), responseEntity);
	}
}
