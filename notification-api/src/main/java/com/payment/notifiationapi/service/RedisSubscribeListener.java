package com.payment.notifiationapi.service;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.common.dto.NotificationDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSubscribeListener implements MessageListener {

	private final RedisTemplate<String, Object> template;
	private final ObjectMapper objectMapper;

	@Override
	public void onMessage(Message message, byte[] pattern) {
		try {
			String publishMessage = template
				.getStringSerializer().deserialize(message.getBody());

			NotificationDto messageDto = objectMapper.readValue(publishMessage, NotificationDto.class);

			log.info("Redis Subscribe Channel : ", messageDto.getRecipient());
			log.info("Redis SUB Message : {}", publishMessage);

			// Return || Another Method Call (etc.sae to DB)
			// TODO
			/*
			 * 여기 알림이 들어갈 예정이다.
			 *
			 * */
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
		}
	}
}