package com.payment.notificationapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.payment.config.CommonDatabaseConfig;

@EnableScheduling
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.payment"})
@ComponentScan(basePackages = { "com.payment"})
@Import({CommonDatabaseConfig.class})
public class NotificationApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(NotificationApiApplication.class, args);
	}
}