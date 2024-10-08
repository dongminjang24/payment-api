package com.payment.model.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.payment.common.dto.ConvertPaymentRequestDto;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.common.enum_type.PayType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "payment")
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

	@Builder.Default
	@Column(nullable = false)
	private boolean paySuccessYN = false;

	@JsonIgnore
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "customer_id")
	private Member customer;

	@Column
	private String paymentKey;
	@Column
	private String failReason;

	@Column
	private boolean cancelYN;
	@Column
	private String cancelReason;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "payment")
	private List<Notification> notifications; // 다대일 관계를 유지하려면 List 형태로 변경해야 합니다.

	@Enumerated(EnumType.STRING)
	private NotificationStatus notificationStatus = NotificationStatus.PENDING;



	public ConvertPaymentRequestDto toPaymentRequestDto() {
		return ConvertPaymentRequestDto.builder()
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
