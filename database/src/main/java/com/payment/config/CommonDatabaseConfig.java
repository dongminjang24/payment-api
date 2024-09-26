package com.payment.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.payment.repository")
@EntityScan(basePackages = "com.payment.model.entity")
@EnableJpaAuditing
public class CommonDatabaseConfig {
	// 필요한 경우 추가 설정
}
