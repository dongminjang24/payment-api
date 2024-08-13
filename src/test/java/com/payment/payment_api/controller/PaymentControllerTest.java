package com.payment.payment_api.controller;

import static org.assertj.core.api.BDDAssumptions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.payment_api.config.TossPaymentConfig;
import com.payment.payment_api.model.dto.PaymentDto;
import com.payment.payment_api.model.dto.PaymentRequestDto;
import com.payment.payment_api.model.dto.PaymentSuccessDto;
import com.payment.payment_api.model.entity.Member;
import com.payment.payment_api.model.entity.Payment;
import com.payment.payment_api.model.enum_type.PayType;
import com.payment.payment_api.repository.MemberRepository;
import com.payment.payment_api.repository.PaymentRepository;
import com.payment.payment_api.service.MemberService;
import com.payment.payment_api.service.TossPaymentServiceImpl;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PaymentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private TossPaymentServiceImpl tossPaymentService;


	@MockBean
	private TossPaymentConfig tossPaymentConfig;

	@MockBean
	private PaymentRepository paymentRepository;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void requestTossPayment() throws Exception {
		// given
		PaymentDto paymentDto = new PaymentDto();
		paymentDto.setPayType(PayType.CARD);
		paymentDto.setAmount(10000L);
		paymentDto.setOrderName("Test Order");
		paymentDto.setEmail("jangdm37@gmail.com");

		Payment payment = paymentDto.toEntity();
		payment.setOrderId("TEST_ORDER_ID");

		Member member = Member.builder()
			.email("jangdm37@gmail.com")
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(0L)
			.build();
		payment.setCustomer(member);

		when(tossPaymentService.requestPayment(any(Payment.class), eq("jangdm37@gmail.com")))
			.thenReturn(payment);
		when(tossPaymentConfig.getSuccessUrl()).thenReturn("http://success.url");
		when(tossPaymentConfig.getFailUrl()).thenReturn("http://fail.url");

		// when & then
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments/toss")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(paymentDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.amount").value(10000))
			.andExpect(jsonPath("$.data.orderName").value("Test Order"))
			.andExpect(jsonPath("$.data.orderId").value("TEST_ORDER_ID"))
			.andExpect(jsonPath("$.data.successUrl").value("http://success.url"))
			.andExpect(jsonPath("$.data.failUrl").value("http://fail.url"))
			.andDo(print());

		verify(tossPaymentService).requestPayment(any(Payment.class), eq("jangdm37@gmail.com"));
		verify(tossPaymentConfig).getSuccessUrl();
		verify(tossPaymentConfig).getFailUrl();
	}

	@Test
	void getPaymentSuccess() throws Exception {

		// given
		PaymentSuccessDto payment = PaymentSuccessDto.builder()
			.paymentKey("TEST_PAYMENT_KEY")
			.orderId("TEST_ORDER_ID")
			.version("1.0")
			.orderName("Test Order")
			.currency("KRW")
			.method("CARD")
			.totalAmount("10000")
			.balanceAmount("10000")
			.suppliedAmount("10000")
			.vat("0")
			.status("SUCCESS")
			.requestedAt(LocalDateTime.now().toString())
			.approvedAt(LocalDateTime.now().plusMinutes(3).toString())
			.useEscrow("false")
			.cultureExpense("false")
			.type("NORMAL")
			.build();
		when(tossPaymentService.tossPaymentSuccess(anyString(), anyString(), anyLong()))
			.thenReturn(payment);


		//then
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payments/toss/success")
				.param("orderId", "TEST_ORDER_ID")
				.param("paymentKey", "TEST_PAYMENT_KEY")
				.param("amount", "10000"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.orderName").value("Test Order"))
			.andExpect(jsonPath("$.data.orderId").value("TEST_ORDER_ID"))
			.andDo(print());

	}

	@Test
	void tossPaymentFail() throws Exception {



		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payments/toss/fail")
			.param("code", "400")
			.param("message", "INVALID_PAYMENT")
			.param("orderId", "TEST_ORDER_ID"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.errorCode").value("400"))
			.andExpect(jsonPath("$.data.errorMessage").value("INVALID_PAYMENT"))
			.andExpect(jsonPath("$.data.orderId").value("TEST_ORDER_ID"))
			.andDo(print());

	}

	@Test
	void tossPaymentCancelPoint_success() throws Exception {
		String email = "jangdm37@gmail.com";
		String paymentKey = "TEST_PAYMENT_KEY";
		String cancelReason = "SOME_REASON";

		Map<String, Object> mockResponse = new HashMap<>();
		mockResponse.put("paymentKey", paymentKey);
		mockResponse.put("status", "CANCELED");

		List<Map<String, Object>> cancels = new ArrayList<>();
		Map<String, Object> cancelInfo = new HashMap<>();
		cancelInfo.put("cancelReason",cancelReason);
		cancels.add(cancelInfo);
		mockResponse.put("cancels", cancels);


		when(tossPaymentService.cancelPaymentPoint(email, paymentKey, cancelReason)).thenReturn(mockResponse);


		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payments/toss/cancel/point")
				.param("email", "jangdm37@gmail.com")
				.param("paymentKey", "TEST_PAYMENT_KEY")
				.param("cancelReason", "SOME_REASON"))
			.andExpect(status().isOk())
			.andDo(print());
		verify(tossPaymentService).cancelPaymentPoint(email, paymentKey, cancelReason);

	}

	@Test
	void testTossPaymentCancelPoint() throws Exception {

		String email = "jangdm37@gmail.com";
		Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "paymentId");

		PaymentDto paymentDto = new PaymentDto();
		paymentDto.setPayType(PayType.CARD);
		paymentDto.setAmount(10000L);
		paymentDto.setOrderName("Test Order");
		paymentDto.setEmail(email);

		Payment payment = paymentDto.toEntity();
		payment.setPaymentId(1L);
		payment.setOrderId("TEST_ORDER_ID");

		Member member = Member.builder()
			.email(email)
			.name("Jang")
			.phoneNumber("010-1234-5678")
			.point(0L)
			.build();
		payment.setCustomer(member);

		List<Payment> list = List.of(payment);
		Page<Payment> payments = new PageImpl<>(list, pageable, 1);

		when(tossPaymentService.findAllChargingHistories(email, pageable)).thenReturn(payments);



		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payments/history")
				.param("email", "jangdm37@gmail.com")
				.param("size", "1")
				.param("page", "0")
				.param("sort", "paymentId,desc")
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content[0].orderName").value("Test Order"))
			.andExpect(jsonPath("$.data.content[0].orderId").value("TEST_ORDER_ID"))
			.andDo(print());
	}
}