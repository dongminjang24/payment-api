package com.payment.common.dto;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.payment.common.enum_type.NotificationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class NotificationDto implements Serializable {

	private String message;
	private NotificationStatus status;
	private String sender;
	private String orderId;

	@JsonCreator
	public NotificationDto(
		@JsonProperty("orderId") String orderId,
		@JsonProperty("message") String message,
		@JsonProperty("sender") String sender,
		@JsonProperty("status") NotificationStatus status) {
		this.orderId = orderId;
		this.message = message;
		this.sender = sender;
		this.status = status;
	}
}
