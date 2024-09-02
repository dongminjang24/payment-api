package com.payment.paymentapi.dto;

import java.time.LocalDateTime;

import com.payment.common.enum_type.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
	private Long id;
	private String email;
	private String name;
	private String phoneNumber;
	private Long point;
	private Role role;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

}