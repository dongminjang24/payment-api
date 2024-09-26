package com.payment.notifiationapi.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.payment.common.dto.NotificationDto;

@Service
public class RedisPublisher {
	private final RedisTemplate<String, Object> template;

	public RedisPublisher(RedisTemplate<String, Object> template) {
		this.template = template;
	}

	/*
	 *  특정 채널에 메시지를 전송한다.
	 * */

	/*
	 * Object Publish
	 * */
	public void publish(ChannelTopic topic, NotificationDto notificationDto) {
		template.convertAndSend(topic.getTopic(), notificationDto);
	}

	/*
	 * String Publish
	 * */
	public void publish(ChannelTopic topic, String data) {
		template.convertAndSend(topic.getTopic(), data);
	}
}