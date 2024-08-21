package com.payment.userapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.payment"})
@EnableJpaAuditing
@EntityScan(basePackages = {"com.payment"})
@ComponentScan(basePackages = {"com.payment"})
public class UserApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserApiApplication.class, args);

	}
}