package com.payment.paymentapi.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import com.payment.paymentapi.config.annotation.DistributeLock;
import com.payment.paymentapi.util.CustomSpringELParser;
import com.payment.common.exception.CustomException;
import com.payment.common.exception.ErrorCode;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributeLockAop {
	private static final String REDISSON_KEY_PREFIX = "RLOCK_";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(com.payment.paymentapi.config.annotation.DistributeLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributeLock distributeLock = method.getAnnotation(DistributeLock.class);

		String key = REDISSON_KEY_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributeLock.key());

		RLock rLock = redissonClient.getLock(key);

		try {
			boolean isLocked = rLock.tryLock(distributeLock.waitTime(), distributeLock.timeUnit());
			if (!isLocked) {
				log.warn("Failed to acquire lock for key: {}", key);
				throw new CustomException(ErrorCode.LOCK_ACQUISITION_FAILURE);
			}

			log.info("Lock acquired successfully: {}", key);
			return aopForTransaction.proceed(joinPoint);
		} catch (DataIntegrityViolationException e) {
			log.error("DataIntegrityViolationException", e);
			throw e;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Lock acquisition was interrupted", e);
			throw new CustomException(ErrorCode.LOCK_INTERRUPTED);
		} catch (Exception e) {
			log.error("Unexpected error occurred while holding the lock", e);
			throw e;
		} finally {
			if (rLock.isHeldByCurrentThread()) {
				rLock.unlock();
				log.info("Lock released: {}", key);
			}
		}
	}
}