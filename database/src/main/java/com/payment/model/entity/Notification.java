package com.payment.model.entity;


import com.payment.common.enum_type.NotificationStatus;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification  extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id", nullable = false, unique = true)
	private Long id;

	@Column(name = "order_id", nullable = false)
	private String orderId;

	private String message;

	private String recipient;

	@Enumerated(EnumType.STRING)
	private NotificationStatus status = NotificationStatus.PENDING;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id") // Payment 엔티티의 PK를 참조합니다.
	private Payment payment;

	public void updateStatus(NotificationStatus status) {
		this.status = status;
	}

}