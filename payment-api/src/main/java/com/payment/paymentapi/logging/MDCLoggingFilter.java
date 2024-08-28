package com.payment.paymentapi.logging;

import java.io.IOException;
import java.util.UUID;

import org.jboss.logging.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class MDCLoggingFilter implements Filter {

	private static final String REQUEST_ID_MDC_KEY = "request_id";
	private static final String REQUEST_ID_HEADER = "X-Request-Id";

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

		String requestId = httpServletRequest.getHeader(REQUEST_ID_HEADER);

		if (requestId == null || requestId.isEmpty()) {
			requestId = UUID.randomUUID().toString();
		}

		try {
			// MDC에 request_id 및 기타 정보를 추가
			MDC.put(REQUEST_ID_MDC_KEY, requestId);

			// 요청 속성에 request_id 추가
			httpServletRequest.setAttribute(REQUEST_ID_MDC_KEY, requestId);

			// 응답 헤더에 request_id 추가
			httpServletResponse.setHeader(REQUEST_ID_HEADER, requestId);

			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			// MDC에서 사용한 모든 키 제거
			MDC.remove(REQUEST_ID_MDC_KEY);
		}
	}
}
