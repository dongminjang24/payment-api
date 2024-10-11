package com.payment.notificationapi.batch;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.payment.common.config.properties.BatchProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationBatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job notificationJob;
	private final BatchProperties batchProperties;

	// api 호출을 통해 트리거를 하거나, 카프카를 통해서 트리거를 할 수 있음.
	@Scheduled(fixedRate = 300000) // 스케줄러를 쓰되,스스로 트리거링이 되기 때문에 제어할 수 없음.
	public void runBatch() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder()
			.addString("createdAt", batchProperties.getCreatedAt())
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
		jobLauncher.run(notificationJob, jobParameters);
	}
}