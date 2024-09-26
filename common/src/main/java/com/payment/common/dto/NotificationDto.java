package com.payment.common.dto;

import java.io.Serial;
import java.io.Serializable;

import com.payment.common.enum_type.NotificationStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationDto implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String message;
	private NotificationStatus status;
	private String sender;
	private String recipient; // 타겟 channel
	private String orderId;

	@Builder
	public NotificationDto(String message,NotificationStatus status, String sender, String recipient, String orderId) {
		this.message = message;
		this.status = status;
		this.sender = sender;
		this.recipient = recipient;
		this.orderId = orderId;
	}


}
