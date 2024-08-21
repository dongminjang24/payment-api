package com.payment.common.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.payment.common.model.dto.PaymentRequestDto;
import com.payment.common.model.enum_type.PayType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Table(indexes = {
	@Index(name = "idx_payment_member", columnList = "customer"),
	@Index(name = "idx_payment_paymentKey", columnList = "paymentKey"),
})
public class Payment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id", nullable = false, unique = true)
	private Long paymentId;

	@Column(nullable = false, name = "pay_type")
	@Enumerated(EnumType.STRING)
	private PayType payType;

	@Column(nullable = false, name = "pay_amount")
	private Long amount;

	@Column(nullable = false, name = "pay_name")
	private String orderName;

	@Column(nullable = false, name = "order_id")
	private String orderId;

	@Column(nullable = false)
	private boolean paySuccessYN = false;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "customer")
	private Member customer;

	@Column
	private String paymentKey;
	@Column
	private String failReason;

	@Column
	private boolean cancelYN;
	@Column
	private String cancelReason;

	public PaymentRequestDto toPaymentResDto() {
		return PaymentRequestDto.builder()
			.payType(payType.toString())
			.amount(amount)
			.orderName(orderName)
			.orderId(orderId)
			.customerEmail(customer.getEmail())
			.customerName(customer.getName())
			.createdAt(String.valueOf(customer.getCreatedAt()))
			.cancelYN(cancelYN)
			.failReason(failReason)
			.build();
	}


}
