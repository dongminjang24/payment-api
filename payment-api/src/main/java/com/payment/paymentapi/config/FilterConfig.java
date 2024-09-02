package com.payment.paymentapi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.payment.paymentapi.logging.MDCLoggingFilter;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<MDCLoggingFilter> mdcLoggingFilter() {
		FilterRegistrationBean<MDCLoggingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new MDCLoggingFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return registrationBean;
	}
}