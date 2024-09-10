package com.payment.paymentapi.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.payment.common.config.properties.SlowQueryMonitoringProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SlowQueryMonitoringAspect {

	private final SlowQueryMonitoringProperties slowQueryMonitoringProperties;

	@Around("execution(* javax.sql.DataSource.getConnection(..)) || " +
		"execution(* org.springframework.jdbc.core.JdbcTemplate.*(..)) || " +
		"execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate.*(..))")
	public Object monitorQuery(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - startTime;

		if (executionTime > slowQueryMonitoringProperties.getSlowQueryThreshold()) {
			String methodName = joinPoint.getSignature().toShortString();
			log.warn("Slow query detected: {} took {} ms", methodName, executionTime);
		}

		return result;
	}
}
