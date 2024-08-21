package com.payment.paymentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.payment.common.repository"})
@EnableJpaAuditing
@EntityScan(basePackages = {"com.payment.common.model.entity"})
@ComponentScan(basePackages = {"com.payment.paymentapi", "com.payment.common"})
public class PaymentApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaymentApiApplication.class, args);
	}
}