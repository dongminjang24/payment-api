package com.payment.paymentapi.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.payment.common.exception.CustomException;
import com.payment.common.exception.PaymentErrorCode;
import com.payment.common.exception.UserErrorCode;
import com.payment.common.model.constants.CacheKey;
import com.payment.common.model.dto.MemberDto;
import com.payment.common.model.dto.PaymentSuccessDto;
import com.payment.common.model.entity.Member;
import com.payment.common.repository.PaymentRepository;
import com.payment.common.service.MemberService;
import com.payment.paymentapi.config.TossPaymentConfig;

import com.payment.common.model.entity.Payment;
import com.payment.paymentapi.util.OrderNumberGenerator;
import com.payment.common.repository.MemberRepository;
import com.payment.common.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TossPaymentServiceImpl implements PaymentService {

	private final RestTemplate restTemplate;
	private final PaymentRepository paymentRepository;
	private final MemberRepository memberRepository;
	private final TossPaymentConfig tossPaymentConfig;

	@Override
	public Payment requestPayment(Payment payment, String userEmail) {
		Member member = memberRepository.findByEmail(userEmail)
			.orElseThrow(() -> new CustomException(UserErrorCode.MEMBER_NOT_FOUND));
		log.debug("Member: {}", member);
		if (payment.getAmount() < 1000) {
			throw new CustomException(PaymentErrorCode.INVALID_PAYMENT_AMOUNT);
		}
		//
		OrderNumberGenerator orderNumberGenerator = new OrderNumberGenerator();
		String orderNumber = orderNumberGenerator.generateOrderNumber();
		payment.setOrderId(orderNumber);
		payment.setCustomer(member);

		return paymentRepository.save(payment);
	}

	@Override
	@Transactional
	public PaymentSuccessDto paymentSuccess(String paymentKey, String orderId, Long amount) {

		Payment payment = verifyPayment(orderId, amount);

		PaymentSuccessDto result = requestPaymentAccept(paymentKey, orderId, amount);

		payment.setPaymentKey(paymentKey);

		payment.setPaySuccessYN(true);

		payment.getCustomer().updatePoint(payment.getCustomer().getPoint() + amount);

		memberRepository.save(payment.getCustomer());
		return result;
	}

	@Override
	public Payment verifyPayment(String orderId, Long amount) {

		Payment payment = paymentRepository.findByOrderId(orderId)
			.orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));

		if (!payment.getAmount().equals(amount)) {
			throw new CustomException(PaymentErrorCode.PAYMENT_AMOUNT_EXP);
		}

		return payment;
	}

	@Override
	public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
		HttpHeaders headers = getHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, Object> params = new HashMap<>();
		params.put("paymentKey", paymentKey);
		params.put("orderId", orderId);
		params.put("amount", amount);

		PaymentSuccessDto result;

		try {
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
			result = restTemplate.postForObject(
				TossPaymentConfig.URL + "confirm",
				requestEntity,
				PaymentSuccessDto.class
			);
			log.debug("Response: {}", result);
		} catch (HttpClientErrorException e) {
			log.error("Payment confirmation failed. Status code: {}, Response body: {}",
				e.getStatusCode(), e.getResponseBodyAsString());
			throw new CustomException(PaymentErrorCode.PAYMENT_CONFIRMATION_FAILED);
		} catch (Exception e) {
			log.error("Unexpected error during payment confirmation", e);
			throw new CustomException(PaymentErrorCode.UNEXPECTED_PAYMENT_ERROR);
		}
		return result;
	}

	/**
	 * Toss Payments API 요청에 필요한 HTTP 헤더를 생성합니다.
	 * @return 설정된 HTTP 헤더
	 */

	private HttpHeaders getHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();

		// 형식: "Base64Encode(clientApiKey:)"
		String rawAuthKey = tossPaymentConfig.getSecretKey() + ":";
		String encodedAuthKey = new String(
			Base64.getEncoder().encode((tossPaymentConfig.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
		log.debug("Raw Auth Key: {}", rawAuthKey);
		log.debug("Encoded Auth Key: {}", encodedAuthKey);
		// 기본 인증정보를 설정
		httpHeaders.setBasicAuth(encodedAuthKey);

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Accept 헤더를 application/json으로 설정
		// 서버로부터 JSON 형식의 응답을 요청함
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		return httpHeaders;
	}

	@Override
	@Transactional
	public void paymentFail(String code, String message, String orderId) {
		Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(
			() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND)
		);


		payment.setPaySuccessYN(false);
		payment.setFailReason(message);
	}

	@Override
	@Transactional
	public Map cancelPaymentPoint(String email, String paymentKey, String cancelReason) {
		Payment payment = paymentRepository.findByPaymentKeyAndCustomer_Email(paymentKey,email).orElseThrow(
			() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND)
		);

		if (payment.getCustomer().getPoint() >= payment.getAmount()) {
			payment.setCancelYN(true);
			payment.setCancelReason(cancelReason);
			long resultPoint = payment.getCustomer().getPoint() - payment.getAmount();
			payment.getCustomer().updatePoint(resultPoint);
			return tossPaymentCancel(paymentKey, cancelReason);
		}

		throw new CustomException(PaymentErrorCode.PAYMENT_NOT_ENOUGH_POINT);
	}

	private Map tossPaymentCancel(String paymentKey, String cancelReason) {

		HttpHeaders headers = getHeaders();
		Map<String, Object> params = new HashMap<>();
		HttpEntity<Map<String, Object>> mapHttpEntity = new HttpEntity<>(params, headers);
		params.put("cancelReason", cancelReason);
		return restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
			mapHttpEntity, Map.class);

	}

	@Override
	@Transactional
	@Cacheable(value = CacheKey.HISTORY_PREFIX,
		key = "#email + '::' + #pageable.pageNumber + '::' + #pageable.pageSize",
		unless = "#result.content.isEmpty()")
	public Slice<Payment> findAllChargingHistories(String email, Pageable pageable) {
		verifyMember(email);

		return paymentRepository.findAllByCustomer_Email(email,
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.Direction.DESC, "paymentId"));
	}

	public void verifyMember(String email) {
		memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(UserErrorCode.MEMBER_NOT_FOUND));
	}


}