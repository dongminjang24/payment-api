package com.payment.paymentapi.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import com.payment.common.exception.CustomException;
import com.payment.common.exception.ErrorCode;
import com.payment.common.constants.CacheKey;
import com.payment.model.entity.Member;
import com.payment.model.entity.Payment;
import com.payment.paymentapi.config.TossPaymentConfig;
import com.payment.paymentapi.dto.PaymentDto;
import com.payment.paymentapi.dto.PaymentSliceDto;
import com.payment.paymentapi.dto.PaymentSuccessDto;
import com.payment.paymentapi.util.OrderNumberGenerator;
import com.payment.repository.MemberRepository;
import com.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
	// private final Map<String, RestTemplate> restTemplates;
	private final Map<String, WebClient> webClients;
	private final PaymentRepository paymentRepository;
	private final MemberRepository memberRepository;
	private final TossPaymentConfig tossPaymentConfig;


	public Payment requestPayment(Payment payment, String userEmail) {
		Member member = memberRepository.findByEmail(userEmail)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
		log.debug("Member: {}", member);
		if (payment.getAmount() < 1000) {
			throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
		}
		//
		OrderNumberGenerator orderNumberGenerator = new OrderNumberGenerator();
		String orderNumber = orderNumberGenerator.generateOrderNumber();
		payment.setOrderId(orderNumber);
		payment.setCustomer(member);

		return paymentRepository.save(payment);
	}

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

	public Payment verifyPayment(String orderId, Long amount) {

		Payment payment = paymentRepository.findByOrderId(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

		if (!payment.getAmount().equals(amount)) {
			throw new CustomException(ErrorCode.PAYMENT_AMOUNT_EXP);
		}

		return payment;
	}

	public PaymentSuccessDto requestPaymentAccept(String paymentKey, String orderId, Long amount) {
		HttpHeaders headers = getHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, Object> params = new HashMap<>();
		params.put("paymentKey", paymentKey);
		params.put("orderId", orderId);
		params.put("amount", amount);

		PaymentSuccessDto result;

		try {
			// HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);
			// RestTemplate tossPaymentsRestTemplate = restTemplates.get("tossPayments");
			// result = tossPaymentsRestTemplate.postForObject(
			// 	TossPaymentConfig.URL + "confirm",
			// 	requestEntity,
			// 	PaymentSuccessDto.class
			// );

			result = webClients.get("tossPayments")
				.post()
				.uri(TossPaymentConfig.URL + "confirm")
				.headers(httpHeaders -> httpHeaders.addAll(headers))
				.bodyValue(params)
				.retrieve()
				.bodyToMono(PaymentSuccessDto.class)
				.block();

			log.debug("Response: {}", result);
		} catch (HttpClientErrorException e) {
			log.error("Payment confirmation failed. Status code: {}, Response body: {}",
				e.getStatusCode(), e.getResponseBodyAsString());
			throw new CustomException(ErrorCode.PAYMENT_CONFIRMATION_FAILED);
		} catch (Exception e) {
			log.error("Unexpected error during payment confirmation", e);
			throw new CustomException(ErrorCode.UNEXPECTED_PAYMENT_ERROR);
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

	@Transactional
	public void paymentFail(String code, String message, String orderId) {
		Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(
			() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND)
		);


		payment.setPaySuccessYN(false);
		payment.setFailReason(message);
	}

	@Async("threadPoolTaskExecutor")
	@Transactional
	public CompletableFuture<Map> cancelPaymentPoint(String email, String paymentKey, String cancelReason) {
		return CompletableFuture.supplyAsync(() -> {
			Payment payment = paymentRepository.findByPaymentKeyAndCustomer_Email(paymentKey, email)
				.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

			if (payment.getCustomer().getPoint() >= payment.getAmount()) {
				payment.setCancelYN(true);
				payment.setCancelReason(cancelReason);
				long resultPoint = payment.getCustomer().getPoint() - payment.getAmount();
				payment.getCustomer().updatePoint(resultPoint);
				return tossPaymentCancel(paymentKey, cancelReason);
			}
			throw new CustomException(ErrorCode.PAYMENT_NOT_ENOUGH_POINT);
		});
	}


	private Map tossPaymentCancel(String paymentKey, String cancelReason) {

		HttpHeaders headers = getHeaders();
		Map<String, Object> params = new HashMap<>();
		// HttpEntity<Map<String, Object>> mapHttpEntity = new HttpEntity<>(params, headers);
		params.put("cancelReason", cancelReason);
		// RestTemplate tossPaymentsRestTemplate = restTemplates.get("tossPayments");
		// Map map = tossPaymentsRestTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
		// 	mapHttpEntity, Map.class);

		return webClients.get("tossPayments")
			.post()
			.uri(TossPaymentConfig.URL + paymentKey + "/cancel")
			.headers(httpHeaders -> httpHeaders.addAll(headers))
			.bodyValue(params)
			.retrieve()
			.bodyToMono(Map.class)
			.block();

	}


	@Transactional(readOnly = true)
	@Cacheable(value = CacheKey.HISTORY_PREFIX, unless = "#result.content.isEmpty()")
	public PaymentSliceDto findAllChargingHistories(String email, Pageable pageable) {
		verifyMember(email);

		Slice<Payment> paymentSlice = paymentRepository.findAllByCustomer_Email(email,
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.Direction.DESC, "paymentId"));

		List<PaymentDto> paymentDTOs = paymentSlice.getContent().stream()
			.map(this::convertToPaymentDto)
			.collect(Collectors.toList());

		return PaymentSliceDto.builder()
			.content(paymentDTOs)
			.hasNext(paymentSlice.hasNext())
			.number(paymentSlice.getNumber())
			.size(paymentSlice.getSize())
			.build();
	}

	private PaymentDto convertToPaymentDto(Payment payment) {
		return PaymentDto.builder()
			.payType(payment.getPayType())
			.amount(payment.getAmount())
			.orderName(payment.getOrderName())
			.email(payment.getCustomer().getEmail())
			.successUrl(null)
			.failUrl(null)
			.build();
	}


	public void verifyMember(String email) {
		memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

}
