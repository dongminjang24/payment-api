package com.payment.payment_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id", nullable = false, unique = true)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String name;

	// 필요에 따라 추가 필드
	private String phoneNumber;

	@JsonIgnore
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Payment> payments = new ArrayList<>();

	@Column(nullable = false) // 포인트
	@ColumnDefault("0")
	private Long point;

	public void updatePoint(Long point) {
		this.point = point;
	}

	public void addPayment(Payment payment) {
		payments.add(payment);
		payment.setCustomer(this);
	}

	public void removePayment(Payment payment) {
		payments.remove(payment);
		payment.setCustomer(null);
	}
}