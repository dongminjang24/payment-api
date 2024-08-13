package com.payment.payment_api.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.payment.payment_api.config.TossPaymentConfig;

import com.payment.payment_api.model.dto.PaymentDto;
import com.payment.payment_api.model.dto.PaymentSuccessDto;
import com.payment.payment_api.model.entity.Member;
import com.payment.payment_api.model.entity.Payment;
import com.payment.payment_api.model.enum_type.PayType;
import com.payment.payment_api.repository.MemberRepository;
import com.payment.payment_api.repository.PaymentRepository;


@SpringBootTest
class TossPaymentServiceImplTest {

	@Autowired
	TossPaymentServiceImpl tossPaymentService;

	@MockBean
	PaymentRepository paymentRepository;

	@MockBean
	MemberRepository memberRepository;

	@MockBean
	TossPaymentConfig tossPaymentConfig;

	@MockBean
	MemberService memberService;

	@MockBean
	RestTemplate restTemplate;


	@BeforeEach
	void setUp() {


		when(tossPaymentConfig.getClientApiKey()).thenReturn("test-client-api-key");
		when(tossPaymentConfig.getSecretKey()).thenReturn("test-secret-key");
		when(tossPaymentConfig.getSuccessUrl()).thenReturn("http://test-success-url");
		when(tossPaymentConfig.getFailUrl()).thenReturn("http://test-fail-url");
	}



	@Test
	void requestPayment() {
		String userEmail = "jangdm37@gmail.com";

		PaymentDto paymentDto = new PaymentDto();
		paymentDto.setPayType(PayType.CARD);
		paymentDto.setAmount(10000L);
		paymentDto.setOrderName("Test Order");
		paymentDto.setEmail(userEmail);

		Payment payment = paymentDto.toEntity();
		payment.setOrderId("TEST_ORDER_ID");

		Member member = Member.builder()
			.email(userEmail)
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(0L)
			.build();
		payment.setCustomer(member);
		when(memberRepository.findByEmail(userEmail)).thenReturn(Optional.of(member));
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

		Payment result = tossPaymentService.requestPayment(payment, userEmail);
		assertNotNull(result);
		assertEquals(10000L, result.getAmount());
		assertEquals("Test Order", result.getOrderName());
		assertNotNull(result.getOrderId());
		assertEquals(member, result.getCustomer());
		verify(memberRepository, times(1)).findByEmail(userEmail);
		verify(paymentRepository, times(1)).save(any(Payment.class));
	}

	@Test
	void tossPaymentSuccess() {
		// Arrange
		String paymentKey = "TEST_PAYMENT_KEY";
		String orderId = "TEST_ORDER_ID";
		Long amount = 10000L;
		String confirmUrl = "https://api.tosspayments.com/v1/payments/confirm";

		Member member = Member.builder()
			.email("jangdm37@gmail.com")
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(0L)
			.build();

		Payment payment = Payment.builder()
			.payType(PayType.CARD)
			.amount(amount)
			.orderName("Test Order")
			.orderId(orderId)
			.customer(member)
			.paySuccessYN(false)
			.build();

		PaymentSuccessDto mockSuccessDto = PaymentSuccessDto.builder()
			.paymentKey(paymentKey)
			.orderId(orderId)
			.version("1.0")
			.status("SUCCESS")
			.totalAmount("10000")
			.approvedAt(LocalDateTime.now().plusMinutes(3).toString())
			.build();

		when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
		when(restTemplate.postForObject(
			eq(confirmUrl),
			any(HttpEntity.class),
			eq(PaymentSuccessDto.class)
		)).thenReturn(mockSuccessDto);

		PaymentSuccessDto result = tossPaymentService.tossPaymentSuccess(paymentKey, orderId, amount);

		assertNotNull(result);
		assertEquals(paymentKey, result.getPaymentKey());
		assertEquals(orderId, result.getOrderId());

		assertEquals(paymentKey, payment.getPaymentKey());
		assertTrue(payment.isPaySuccessYN());
		assertEquals(amount, member.getPoint());

		verify(memberService).saveMember(member);
		verify(restTemplate).postForObject(
			eq(confirmUrl),
			any(HttpEntity.class),
			eq(PaymentSuccessDto.class)
		);
	}
	@Test
	void verifyPayment() {
		String orderId = "TEST_ORDER_ID";
		Long amount = 10000L;

		Member member = Member.builder()
			.email("jangdm37@gmail.com")
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(0L)
			.build();

		Payment payment = Payment.builder()
			.payType(PayType.CARD)
			.amount(amount)
			.orderName("Test Order")
			.orderId(orderId)
			.customer(member)
			.paySuccessYN(false)
			.build();
		when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));


		Payment result = tossPaymentService.verifyPayment(orderId, amount);
		assertNotNull(result);
		assertEquals(orderId, result.getOrderId());
		assertEquals(amount, result.getAmount());
		verify(paymentRepository).findByOrderId(orderId);


	}

	@Test
	void requestPaymentAccept() {
		String paymentKey = "TEST_PAYMENT_KEY";
		String orderId = "TEST_ORDER_ID";
		Long amount = 10000L;
		String confirmUrl = "https://api.tosspayments.com/v1/payments/confirm";

		Member member = Member.builder()
			.email("jangdm37@gmail.com")
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(0L)
			.build();

		Payment payment = Payment.builder()
			.payType(PayType.CARD)
			.amount(amount)
			.orderName("Test Order")
			.orderId(orderId)
			.customer(member)
			.paySuccessYN(false)
			.build();

		PaymentSuccessDto mockSuccessDto = PaymentSuccessDto.builder()
			.paymentKey(paymentKey)
			.orderId(orderId)
			.version("1.0")
			.status("SUCCESS")
			.totalAmount("10000")
			.approvedAt(LocalDateTime.now().plusMinutes(3).toString())
			.build();

		when(restTemplate.postForObject(
			eq(confirmUrl),
			any(HttpEntity.class),
			eq(PaymentSuccessDto.class)
		)).thenReturn(mockSuccessDto);

		PaymentSuccessDto result = tossPaymentService.requestPaymentAccept(paymentKey, orderId, amount);

		assertNotNull(result);
		assertEquals(paymentKey, result.getPaymentKey());
		assertEquals(orderId, result.getOrderId());
		assertEquals("SUCCESS", result.getStatus());
		assertEquals("10000", result.getTotalAmount());
	}

	@Test
	void tossPaymentFail() {
		String orderId = "TEST_ORDER_ID";

		Long amount = 10000L;

		Member member = Member.builder()
			.email("jangdm37@gmail.com")
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(0L)
			.build();

		Payment payment = Payment.builder()
			.payType(PayType.CARD)
			.amount(amount)
			.orderName("Test Order")
			.orderId(orderId)
			.customer(member)
			.paySuccessYN(false)
			.build();
		String message = "TEST_FAIL_MESSAGE";
		when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

		tossPaymentService.tossPaymentFail(null,message, payment.getOrderId());

		assertFalse(payment.isPaySuccessYN());
		assertEquals(message, payment.getFailReason());

		verify(paymentRepository).findByOrderId(orderId);

	}

	@Test
	void cancelPaymentPoint() {
		String orderId = "TEST_ORDER_ID";
		Long amount = 5000L;
		String paymentKey = "TEST_PAYMENT_KEY";
		String fail_reason = "TEST_FAIL_REASON";

		Member member = Member.builder()
			.email("jangdm37@gmail.com")
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(10000L)
			.build();

		Payment payment = Payment.builder()
			.payType(PayType.CARD)
			.paymentKey(paymentKey)
			.amount(amount)
			.orderName("Test Order")
			.orderId(orderId)
			.customer(member)
			.paySuccessYN(false)
			.build();
		when(paymentRepository.findByPaymentKeyAndCustomer_Email(payment.getPaymentKey(), payment.getCustomer().getEmail())).thenReturn(Optional.of(payment));

		tossPaymentService.cancelPaymentPoint("jangdm37@gmail.com",paymentKey , fail_reason);

		assertTrue(payment.isCancelYN());
		assertFalse(payment.isPaySuccessYN());
		assertEquals(fail_reason, payment.getCancelReason());
		assertEquals(5000L, member.getPoint());

		verify(paymentRepository).findByPaymentKeyAndCustomer_Email(payment.getPaymentKey(), payment.getCustomer().getEmail());

	}

	@Test
	void findAllChargingHistories() {
		String orderId = "TEST_ORDER_ID";
		Long amount = 5000L;
		String paymentKey = "TEST_PAYMENT_KEY";
		String fail_reason = "TEST_FAIL_REASON";

		Member member = Member.builder()
			.email("jangdm37@gmail.com")
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(10000L)
			.build();

		Payment payment = Payment.builder()
			.payType(PayType.CARD)
			.paymentKey(paymentKey)
			.amount(amount)
			.orderName("Test Order")
			.orderId(orderId)
			.customer(member)
			.paySuccessYN(false)
			.build();
		PageRequest pageRequest = PageRequest.of(0, 1, Sort.Direction.DESC, "paymentId");
		when(paymentRepository.findAllByCustomer_Email(payment.getCustomer().getEmail(), pageRequest)).thenReturn(new PageImpl<>(
			List.of(payment), pageRequest, 1));

		tossPaymentService.findAllChargingHistories(member.getEmail(), pageRequest);


		verify(paymentRepository).findAllByCustomer_Email(payment.getCustomer().getEmail(), pageRequest);
		verify(memberService).verifyMember(member.getEmail());

	}
}