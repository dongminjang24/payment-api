package com.payment.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

	@Bean("masterDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.master")
	public DataSource masterDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean("slaveDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.slave")
	public DataSource slaveDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

	@Bean
	@Primary
	public DataSource routingDataSource(
		@Qualifier("masterDataSource") DataSource masterDataSource,
		@Qualifier("slaveDataSource") DataSource slaveDataSource) {

		RoutingDataSource routingDataSource = new RoutingDataSource();

		Map<Object, Object> targetDataSources = new HashMap<>();
		targetDataSources.put("master", masterDataSource);
		targetDataSources.put("slave", slaveDataSource);

		routingDataSource.setTargetDataSources(targetDataSources);
		routingDataSource.setDefaultTargetDataSource(masterDataSource);
		routingDataSource.afterPropertiesSet();

		return new LazyConnectionDataSourceProxy(routingDataSource);
	}
}