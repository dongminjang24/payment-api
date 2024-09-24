package com.payment.paymentapi.service;

import static com.payment.common.exception.ErrorCode.*;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import com.payment.common.exception.CustomException;
import com.payment.model.entity.Notification;
import com.payment.paymentapi.dto.NotificationDto;
import com.payment.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPubService {
	private final RedisMessageListenerContainer redisMessageListenerContainer;
	private final RedisPublisher redisPublisher;
	private final NotificationRepository notificationRepository;

	// 각 Channel 별 Listener
	private final RedisSubscribeListener redisSubscribeListener;

	/*
	 * Channel 별 Message 전송
	 * @Param
	 * */
	public void pubMsgChannel(String channel, NotificationDto notificationDto) {
		// 1. 요청한 Channel을 구독
		redisMessageListenerContainer.addMessageListener(redisSubscribeListener,
			new ChannelTopic(channel));



		// 2. Message 전송
		redisPublisher.publish(new ChannelTopic(channel), notificationDto);

		// 3. Message 저장
		notificationRepository.save(Notification.builder()
			.orderId(notificationDto.getOrderId())
			.recipient(notificationDto.getRecipient())
				.status(notificationDto.getStatus())
			.message(notificationDto.getMessage())

			.build());
	}

	/*
	 * Channel 구독 취소
	 * @Param channel
	 * */
	public void cancelSubChannel(String channel) {
		redisMessageListenerContainer.removeMessageListener(redisSubscribeListener);
	}
}
