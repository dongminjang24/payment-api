package com.payment.paymentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = {"com.payment"})
@EnableJpaRepositories(basePackages = {"com.payment"})
@EnableJpaAuditing
@EntityScan(basePackages = {"com.payment"})
@ComponentScan(basePackages = { "com.payment"})
public class PaymentApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaymentApiApplication.class, args);
	}
}