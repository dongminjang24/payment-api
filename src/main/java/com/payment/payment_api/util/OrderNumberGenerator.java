package com.payment.payment_api.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class OrderNumberGenerator {
	private final SnowflakeIdGenerator snowflake;

	public OrderNumberGenerator(long datacenterId, long workerId) {
		this.snowflake = new SnowflakeIdGenerator(datacenterId, workerId);
	}

	public String generateOrderNumber() {
		long snowflakeId = snowflake.nextId();
		// 날짜 부분 (5자리)과 고유 부분 (7자리)를 조합하여 12자리 주문번호 생성
		String datePart = generateCompressedDatePart(snowflakeId);
		String uniquePart = String.format("%07d", snowflakeId % 10000000L);
		return datePart + uniquePart;
	}

	private String generateCompressedDatePart(long snowflakeId) {
		// 스노우플레이크 ID에서 타임스탬프 추출
		long timestamp = (snowflakeId >> 22) + SnowflakeIdGenerator.TWEPOCH;
		LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();

		// 년도의 마지막 두 자리
		int year = date.getYear() % 100;
		// 해당 년도의 일 수 (1-366)
		int dayOfYear = date.getDayOfYear();

		// YY(년도 2자리)DDD(해당 년도의 일 수 3자리) 형식으로 반환
		return String.format("%02d%03d", year, dayOfYear);
	}

	// 스노우플레이크 ID 생성기 내부 클래스
	private static class SnowflakeIdGenerator {
		// 2010-11-04 09:42:54.657 UTC - 스노우플레이크 시작 시간
		private static final long TWEPOCH = 1288834974657L;

		// 각 부분의 비트 수 정의
		private static final long WORKER_ID_BITS = 5L;
		private static final long DATACENTER_ID_BITS = 5L;
		private static final long SEQUENCE_BITS = 12L;

		// 최대값 계산
		private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
		private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);

		// 비트 시프트 값 계산
		private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
		private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
		private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

		private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

		private long workerId;
		private long datacenterId;
		private long sequence = 0L;
		private long lastTimestamp = -1L;

		public SnowflakeIdGenerator(long datacenterId, long workerId) {
			// 데이터센터 ID와 워커 ID의 유효성 검사
			if (workerId > MAX_WORKER_ID || workerId < 0) {
				throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
			}
			if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
				throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATACENTER_ID + " or less than 0");
			}
			this.workerId = workerId;
			this.datacenterId = datacenterId;
		}

		// 다음 ID 생성
		public synchronized long nextId() {
			long timestamp = timeGen();

			// 시계가 뒤로 갔는지 체크
			if (timestamp < lastTimestamp) {
				throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
			}

			// 같은 밀리초 내에서의 처리
			if (lastTimestamp == timestamp) {
				sequence = (sequence + 1) & SEQUENCE_MASK;
				if (sequence == 0) {
					// 같은 밀리초 내에서 시퀀스를 모두 사용했으면 다음 밀리초까지 대기
					timestamp = tilNextMillis(lastTimestamp);
				}
			} else {
				sequence = 0L;
			}

			lastTimestamp = timestamp;

			// ID 조합: 타임스탬프 + 데이터센터 ID + 워커 ID + 시퀀스
			return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) |
				(datacenterId << DATACENTER_ID_SHIFT) |
				(workerId << WORKER_ID_SHIFT) |
				sequence;
		}

		// 다음 밀리초까지 대기
		private long tilNextMillis(long lastTimestamp) {
			long timestamp = timeGen();
			while (timestamp <= lastTimestamp) {
				timestamp = timeGen();
			}
			return timestamp;
		}

		// 현재 시간을 밀리초로 반환
		private long timeGen() {
			return System.currentTimeMillis();
		}
	}


}
