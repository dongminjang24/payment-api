package com.payment.paymentapi.logging;

import java.util.UUID;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

	public static final String LOG_ID = "logId";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse
		response, Object handler) throws Exception {
		String requestURI = request.getRequestURI();
		String uuid = UUID.randomUUID().toString();
		request.setAttribute(LOG_ID, uuid);
		request.setAttribute("startTime", System.currentTimeMillis());
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			log.info("Handler Method: {}.{}", hm.getBeanType().getSimpleName(), hm.getMethod().getName());
		}
		log.info("REQUEST  [{}][{}][{}][{}]", uuid, request.getDispatcherType(), requestURI, handler);
		return true;
	}


	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse
		response, Object handler, ModelAndView modelAndView) throws Exception {
		log.info("postHandle [{}]", modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse
		response, Object handler, Exception ex) throws Exception {
		String requestURI = request.getRequestURI();
		String logId = (String) request.getAttribute(LOG_ID);
		log.info("RESPONSE [{}][{}][{}][status:{}]", logId, request.getDispatcherType(), requestURI, response.getStatus());
		if (ex != null) {
			log.error("afterCompletion error!!", ex);
		}
		long startTime = (Long) request.getAttribute("startTime");
		long duration = System.currentTimeMillis() - startTime;
		log.info("Request processed in {}ms", duration);
	}


}
