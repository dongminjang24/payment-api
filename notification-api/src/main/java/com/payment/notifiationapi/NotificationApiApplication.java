package com.payment.notifiationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import com.payment.config.CommonDatabaseConfig;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.payment"})
@ComponentScan(basePackages = { "com.payment"})
@Import({CommonDatabaseConfig.class})
public class NotificationApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(NotificationApiApplication.class, args);
	}
}