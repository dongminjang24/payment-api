package com.payment.notificationapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.payment.common.dto.NotificationDto;
import com.payment.common.enum_type.NotificationStatus;
import com.payment.model.entity.Notification;
import com.payment.repository.NotificationRepository;
import com.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationProcessor notificationProcessor;


	// 기존 transaction 어노테이션 제거
	// KAFKA_CREATE_TOPICS: "payment-notifications:1:1"
	/* 커스텀 어노테이션 => 커밋을 찍는 것을 빠뜨리지 않게
	* ack를 여기저기 하다보면 실수가 생길 수 잇음.
	* aop를 활용해서 커스텀 어노테이션을 생성하여
	* kafkalistener어노테이션 (위에 또는 함께) 끝나면 무조건 커밋을 찍는
	* 빠뜨리지 않도록
	*
	* 주석으로 항상 어필을 하기!!!
	* 애매한거 있으면 회사에 문의하기 -> 답장 안올 시에 주석으로 설명
	*  */
	@KafkaListener(topics = "payment-notifications", groupId = "notification-group", concurrency = "1")
	public void listenNotifications(NotificationDto notificationDto, Acknowledgment acknowledgment) {
		log.info("Received notification: {}", notificationDto);
		try {
			notificationProcessor.processNotification(notificationDto);
			acknowledgment.acknowledge();
		} catch (Exception e) {
			log.error("Error processing notification", e);
			acknowledgment.acknowledge();
			/*여기서 dlq를 아예 바로 발행해도 좋음
			* 여기서 정보들을 넣어주는 식으로 dto를 이용해서
			*
			* */
			throw e;
		}
	}


	// 배치 처리를 위한 메서드
	public void processBatchNotification(NotificationDto notificationDto) {
		notificationProcessor.processNotification(notificationDto);
	}
}